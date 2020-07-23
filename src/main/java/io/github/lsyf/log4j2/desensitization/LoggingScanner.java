package io.github.lsyf.log4j2.desensitization;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.*;
import java.util.stream.Collectors;


public class LoggingScanner {

    private Map<String, Config> configMap = new HashMap<>();
    private Set<String> keywords = new HashSet<>();
    private boolean enable;


    private static final String DEFAULT_NAME = "__default__";
    private Config DEFAULT = new Config(DEFAULT_NAME, "num|en|lang", 0, 0, '*', " \t", 5);


    public LoggingScanner(boolean enable, List<Config> configs) {
        try {
            this.enable = enable;
            if (configs == null || configs.size() == 0) {
                return;
            }
            configMap = configs.stream()
                    .filter(Objects::nonNull)
                    .filter(f -> f.getName() != null && f.getName().trim().length() > 0)
                    .peek(f -> {
                        f.setName(f.getName().toLowerCase());
                        keywords.add(f.getName());
                    })
                    .collect(Collectors.toMap(Config::getName, a -> a, (a, b) -> b));
            //自定义默认配置覆盖，并移除map
            if (configMap.containsKey(DEFAULT_NAME)) {
                DEFAULT.override(configMap.get(DEFAULT_NAME));
                configMap.remove(DEFAULT_NAME);
                keywords.remove(DEFAULT_NAME);
            }
            //未配置则使用默认配置
            configMap.forEach((k, v) -> v.extend(DEFAULT));
        } catch (Throwable e) {
            //ignore, do nothing.
        }
    }


    public String processLog(String originMessage) {
        if (!this.enable || originMessage == null || originMessage.length() == 0 || keywords.size() == 0) {
            return originMessage;
        }
        char[] chars = originMessage.toCharArray();
        try {
            Trie trie = Trie.builder().addKeywords(keywords).onlyWholeWords().ignoreCase().build();
            List<Emit> emits = (List<Emit>) trie.parseText(originMessage);
            if (emits.size() == 0) {
                return originMessage;
            }
            //增加一个配置方便计算
            emits.add(new Emit(chars.length, chars.length, ""));

            for (int i = 0; i < emits.size() - 1; i++) {
                Emit emit = emits.get(i);
                String keyword = emit.getKeyword();
                Config config = this.configMap.get(keyword);

                char x = config.getC();
                int maxMissed = config.getMaxMissed();
                boolean isHit = false;
                int skipHead = config.getSkipHead();
                int skipTail = config.getSkipTail();
                int start = -1;
                int end = -1;
                int index;
                int missNum = 0;//未匹配字符数
                int continuousIgnoreNum = 0;//连续跳过字符数
                int curTail = emit.getEnd();//匹配到的字符尾部
                int nextHead = emits.get(i + 1).getStart();//下一个匹配字符的首部
                for (index = curTail + 1; missNum <= maxMissed && index < nextHead; index++) {
                    char c = chars[index];
                    boolean hitIgnoreSymbol = config.hitIgnoreSymbol(c);
                    //命中脱敏内容
                    if (!hitIgnoreSymbol && config.hitContent(c)) {
                        isHit = true;
                        if (start == -1) {
                            start = index;
                        }
                    } else {
                        //如果上个字符符合脱敏内容格式，下一个未命中，则视为结束
                        if (isHit) {
                            end = index;
                            break;
                        } else {
                            if (hitIgnoreSymbol) {
                                //对于免去筛查的字符，连续多个时视为只有一个字符
                                if (++continuousIgnoreNum == 1) {
                                    missNum++;
                                }
                            } else {
                                //既非免筛字符也非脱敏内容，则清空连续免筛数，增加未命中数
                                continuousIgnoreNum = 0;
                                missNum++;
                            }
                        }
                    }
                }
                if (isHit) {
                    if (end == -1) {
                        end = index;
                    }
                    for (int k = start + skipHead; k < end - skipTail; k++) {
                        chars[k] = x;
                    }
                }
            }
        } catch (Throwable e) {
            //ignore, do nothing.
        }
        return new String(chars);
    }


    public static class Config {
        private String name;//脱敏字段名
        private String content;//字段内容格式
        private Integer skipHead;//跳过前n个字符
        private Integer skipTail;//跳过后n个字符
        private Character c;//替换字符
        private String ignoreSymbols;//可跳过的字符
        private HashSet<Character> ignoreSymbolArray = new HashSet<>();
        private Integer maxMissed;//最大未命中数。超过数量则该字段脱敏失败

        private Set<CharType> charTypes = new HashSet<>();//内容格式
        private Set<Character> charArray = new HashSet<>();//字符内容

        public Config(String name,
                      String content,
                      Integer skipHead,
                      Integer skipTail,
                      Character c,
                      String ignoreSymbols,
                      Integer maxMissed) {
            setName(name);
            setContent(content);
            setSkipHead(skipHead);
            setSkipTail(skipTail);
            setC(c);
            setIgnoreSymbols(ignoreSymbols);
            setMaxMissed(maxMissed);

        }

        public void setContent(String content) {
            this.content = content;
            charArray.clear();
            charTypes.clear();
            Optional.ofNullable(content)
                    .ifPresent(ct -> Optional.ofNullable(ct.split("\\|"))
                            .ifPresent(strs -> {
                                for (String s : strs) {
                                    if (s.length() == 1) {
                                        charArray.add(s.charAt(0));
                                        charTypes.add(CharType._char);
                                    } else {
                                        Optional.ofNullable(CharType.nameOf(s.trim().toLowerCase())).ifPresent(x -> charTypes.add(x));
                                    }
                                }
                            }));

        }

        public void setIgnoreSymbols(String ignoreSymbols) {
            this.ignoreSymbols = ignoreSymbols;
            ignoreSymbolArray.clear();
            if (ignoreSymbols != null) {
                for (int i = 0; i < ignoreSymbols.length(); i++) {
                    ignoreSymbolArray.add(ignoreSymbols.charAt(i));
                }
            }
        }

        public void override(Config a) {
            Optional.ofNullable(a.content).ifPresent(x -> this.setContent(x));
            Optional.ofNullable(a.c).ifPresent(x -> this.setC(x));
            Optional.ofNullable(a.maxMissed).ifPresent(x -> this.setMaxMissed(x));
            Optional.ofNullable(a.skipHead).ifPresent(x -> this.setSkipHead(x));
            Optional.ofNullable(a.skipTail).ifPresent(x -> this.setSkipTail(x));
            Optional.ofNullable(a.ignoreSymbols).ifPresent(x -> this.setIgnoreSymbols(x));
        }

        public void extend(Config p) {
            this.setContent(Optional.ofNullable(content).orElse(p.content));
            this.setC(Optional.ofNullable(c).orElse(p.c));
            this.setMaxMissed(Optional.ofNullable(maxMissed).orElse(p.maxMissed));
            this.setSkipHead(Optional.ofNullable(skipHead).orElse(p.skipHead));
            this.setSkipTail(Optional.ofNullable(skipTail).orElse(p.skipTail));
            this.setIgnoreSymbols(Optional.ofNullable(ignoreSymbols).orElse(p.ignoreSymbols));
        }

        public boolean hitIgnoreSymbol(char c) {
            if (charArray.isEmpty()) {
                return false;
            }
            return ignoreSymbolArray.contains(c);
        }


        public boolean hitContent(char c) {
            for (CharType charType : charTypes) {
                switch (charType) {
                    case _char:
                        if (!charArray.isEmpty() && charArray.contains(c)) {
                            return true;
                        }
                        break;
                    case num:
                        if ('0' <= c && c <= '9') {
                            return true;
                        }
                        break;
                    case low:
                        if ('a' <= c && c <= 'z') {
                            return true;
                        }
                        break;
                    case up:
                        if ('A' <= c && c <= 'Z') {
                            return true;
                        }
                        break;
                    case cn:
                        if (19968 <= c && c <= 171941) {
                            return true;
                        }
                        break;
                    case en:
                        if ('a' <= c && c <= 'z'
                                || 'A' <= c && c <= 'Z') {
                            return true;
                        }
                        break;
                    case lang:
                        if (c > 127) {
                            return true;
                        }
                        break;
                }
            }
            return false;
        }

        public String getName() {
            return this.name;
        }

        public String getContent() {
            return this.content;
        }

        public Integer getSkipHead() {
            return this.skipHead;
        }

        public Integer getSkipTail() {
            return this.skipTail;
        }

        public Character getC() {
            return this.c;
        }

        public Integer getMaxMissed() {
            return this.maxMissed;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setSkipHead(final Integer skipHead) {
            this.skipHead = skipHead;
        }

        public void setSkipTail(final Integer skipTail) {
            this.skipTail = skipTail;
        }

        public void setC(final Character c) {
            this.c = c;
        }

        public void setMaxMissed(final Integer maxMissed) {
            this.maxMissed = maxMissed;
        }

    }

    enum CharType {
        cn,
        lang,
        en,
        low,
        up,
        num,
        _char;

        CharType() {
        }

        public static CharType nameOf(String name) {
            try {
                return valueOf(name);
            } catch (Exception var2) {
                return null;
            }
        }
    }
}
