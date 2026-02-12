package org.tact.features.seasons.model;

public enum Season {
    SPRING("Spring", "\uD83C\uDF38", 1.1F, 0.0F, 0.0F, 0.0F),
    SUMMER("Summer", "☀\uFE0F", 1.25F, 0.05F, 0.03F, 0.0F),
    AUTUMN("Autumn", "\uD83C\uDF42", 1.0F, 0.4F, 0.15F, 0.05F),
    WINTER("Winter", "❄\uFE0F", 0.8F, 0.1F, 0.05F, 0.45F);

    private final String displayName;
    private final String icon;
    private final float dayLengthMultiplier;
    private final float rainChance;
    private final float stormChance;
    private final float snowChance;

    Season(
            String displayName,
            String icon,
            float dayLengthMultiplier,
            float rainChance,
            float stormChance,
            float snowChance
    ) {
        this.displayName = displayName;
        this.icon = icon;
        this.dayLengthMultiplier = dayLengthMultiplier;
        this.rainChance = rainChance;
        this.stormChance = stormChance;
        this.snowChance = snowChance;
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
    public float getStormChance() { return stormChance; }
    public float getSnowChance() { return snowChance; }

    public Season next() {
        return values()[(ordinal() + 1) % values().length];
    }

}
