package io.github.lsyf.log4j2.desensitization;

import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;


@Plugin(name = "DesensitizedField", category = Node.CATEGORY, printObject = true)
public final class DesensitizedField {

    private final String name;//脱敏字段名
    private final String content;//字段内容格式
    private final Integer skipHead;//跳过前n个字符
    private final Integer skipTail;//跳过后n个字符
    private final Character c;//替换字符
    private final String ignoreSymbols;//不计入未命中数量的字符
    private final Integer maxMissed;//最大未命中数。超过数量则该字段脱敏失败


    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder implements org.apache.logging.log4j.core.util.Builder<DesensitizedField> {

        @PluginBuilderAttribute
        private String name;//脱敏字段名
        @PluginBuilderAttribute
        private String content;//字段内容格式
        @PluginBuilderAttribute
        private Integer skipHead;//跳过前n个字符
        @PluginBuilderAttribute
        private Integer skipTail;//跳过后n个字符
        @PluginBuilderAttribute
        private Character c;//替换字符
        @PluginBuilderAttribute
        private String ignoreSymbols;//不计入未命中数量的字符
        @PluginBuilderAttribute
        private Integer maxMissed;//最大未命中数。超过数量则该字段脱敏失败

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setContent(final String content) {
            this.content = content;
            return this;
        }

        public Builder setSkipHead(final Integer skipHead) {
            this.skipHead = skipHead;
            return this;
        }

        public Builder setSkipTail(final Integer skipTail) {
            this.skipTail = skipTail;
            return this;
        }

        public Builder setC(final Character c) {
            this.c = c;
            return this;
        }

        public Builder setIgnoreSymbols(final String ignoreSymbols) {
            this.ignoreSymbols = ignoreSymbols;
            return this;
        }

        public Builder setMaxMissed(final Integer maxMissed) {
            this.maxMissed = maxMissed;
            return this;
        }

        @Override
        public DesensitizedField build() {
            return new DesensitizedField(name, content, skipHead, skipTail, c, ignoreSymbols, maxMissed);
        }

    }

    public DesensitizedField(final String name, final String content, final Integer skipHead, final Integer skipTail, final Character c, final String ignoreSymbols, final Integer maxMissed) {
        this.name = name;
        this.content = content;
        this.skipHead = skipHead;
        this.skipTail = skipTail;
        this.c = c;
        this.ignoreSymbols = ignoreSymbols;
        this.maxMissed = maxMissed;
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

    public String getIgnoreSymbols() {
        return this.ignoreSymbols;
    }

    public Integer getMaxMissed() {
        return this.maxMissed;
    }

    @Override
    public String toString() {
        return "DesensitizedField(name=" + this.getName() + ", content=" + this.getContent() + ", skipHead=" + this.getSkipHead() + ", skipTail=" + this.getSkipTail() + ", c=" + this.getC() + ", ignoreSymbols=" + this.getIgnoreSymbols() + ", maxMissed=" + this.getMaxMissed() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DesensitizedField)) {
            return false;
        } else {
            DesensitizedField other;
            label92:
            {
                other = (DesensitizedField) o;
                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name == null) {
                        break label92;
                    }
                } else if (this$name.equals(other$name)) {
                    break label92;
                }

                return false;
            }

            Object this$content = this.getContent();
            Object other$content = other.getContent();
            if (this$content == null) {
                if (other$content != null) {
                    return false;
                }
            } else if (!this$content.equals(other$content)) {
                return false;
            }

            Object this$skipHead = this.getSkipHead();
            Object other$skipHead = other.getSkipHead();
            if (this$skipHead == null) {
                if (other$skipHead != null) {
                    return false;
                }
            } else if (!this$skipHead.equals(other$skipHead)) {
                return false;
            }

            label71:
            {
                Object this$skipTail = this.getSkipTail();
                Object other$skipTail = other.getSkipTail();
                if (this$skipTail == null) {
                    if (other$skipTail == null) {
                        break label71;
                    }
                } else if (this$skipTail.equals(other$skipTail)) {
                    break label71;
                }

                return false;
            }

            label64:
            {
                Object this$c = this.getC();
                Object other$c = other.getC();
                if (this$c == null) {
                    if (other$c == null) {
                        break label64;
                    }
                } else if (this$c.equals(other$c)) {
                    break label64;
                }

                return false;
            }

            Object this$ignoreSymbols = this.getIgnoreSymbols();
            Object other$ignoreSymbols = other.getIgnoreSymbols();
            if (this$ignoreSymbols == null) {
                if (other$ignoreSymbols != null) {
                    return false;
                }
            } else if (!this$ignoreSymbols.equals(other$ignoreSymbols)) {
                return false;
            }

            Object this$maxMissed = this.getMaxMissed();
            Object other$maxMissed = other.getMaxMissed();
            if (this$maxMissed == null) {
                if (other$maxMissed != null) {
                    return false;
                }
            } else if (!this$maxMissed.equals(other$maxMissed)) {
                return false;
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        int result = 1;
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $content = this.getContent();
        result = result * 59 + ($content == null ? 43 : $content.hashCode());
        Object $skipHead = this.getSkipHead();
        result = result * 59 + ($skipHead == null ? 43 : $skipHead.hashCode());
        Object $skipTail = this.getSkipTail();
        result = result * 59 + ($skipTail == null ? 43 : $skipTail.hashCode());
        Object $c = this.getC();
        result = result * 59 + ($c == null ? 43 : $c.hashCode());
        Object $ignoreSymbols = this.getIgnoreSymbols();
        result = result * 59 + ($ignoreSymbols == null ? 43 : $ignoreSymbols.hashCode());
        Object $maxMissed = this.getMaxMissed();
        result = result * 59 + ($maxMissed == null ? 43 : $maxMissed.hashCode());
        return result;
    }

}
