plugins {
    id("java")
}
group = "it.renvins"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}