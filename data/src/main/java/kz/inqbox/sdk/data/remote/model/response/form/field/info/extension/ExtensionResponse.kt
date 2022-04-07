package kz.inqbox.sdk.data.remote.model.response.form.field.info.extension

// TODO: Support PPT, PPTX
enum class ExtensionResponse constructor(val value: String) {
    JPG("jpg"),
    JPEG("jpeg"),

    PNG("png"),

    TXT("txt"),

    DOC("doc"),
    DOCX("docx"),

    XLS("xls"),
    XLSX("xlsx"),

    PDF("pdf"),

    HTML("html"),

    MP3("mp3"),
    M4A("m4a"),
    WAV("wav"),

    THREE_GP("3gp"),
    THREE_GPP("3gpp"),

    AMR("amr"),

    AAC("aac"),

    OPUS("opus"),
    OGG("ogg"),

    FLAC("flac"),

    MP4("mp4"),
    MOV("mov"),
    WEBM("webm"),
    MKV("mkv"),
    AVI("avi")
}