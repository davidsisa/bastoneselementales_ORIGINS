package net.minecraft.util.profiling.metrics;

public enum MetricCategory {
    PATH_FINDING("pathfinding"),
    EVENT_LOOPS("event-loops"),
    MAIL_BOXES("mailboxes"),
    TICK_LOOP("ticking"),
    JVM("jvm"),
    CHUNK_RENDERING("chunk rendering"),
    CHUNK_RENDERING_DISPATCHING("chunk rendering dispatching"),
    CPU("cpu"),
    GPU("gpu");

    private final String description;

    private MetricCategory(final String pDescription) {
        this.description = pDescription;
    }

    public String getDescription() {
        return this.description;
    }
}