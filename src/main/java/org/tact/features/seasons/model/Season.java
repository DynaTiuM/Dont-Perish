package org.tact.features.seasons.model;

public enum Season {
    SPRING("Spring", "\uD83C\uDF38", 0.9F, 0.6F),
    SUMMER("Summer", "☀\uFE0F", 1.3F, 0.1F),
    AUTUMN("Autumn", "\uD83C\uDF42", 1.0F, 0.3F),
    WINTER("Winter", "❄\uFE0F", 0.7F, 0.4F);

    private final String displayName;
    private final String icon;
    private final float dayLengthMultiplier;
    private final float rainChance;

    Season(String displayName, String icon, float dayLengthMultiplier, float rainChance) {
        this.displayName = displayName;
        this.icon = icon;
        this.dayLengthMultiplier = dayLengthMultiplier;
        this.rainChance = rainChance;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    public float getDayLengthMultiplier() {
        return dayLengthMultiplier;
    }

    public float getRainChance() {
        return rainChance;
    }

    public Season next() {
        return values()[(ordinal() + 1) % values().length];
    }

}
