package kz.inqbox.sdk.data.remote.model.response.upload

import kz.inqbox.sdk.domain.model.upload.UploadResult

fun UploadResponse.toUploadResult(): UploadResult =
    UploadResult(hash = hash, title = title, url = url)
