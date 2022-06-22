package kz.qbox.sdk.data.remote.model.response.upload

import kz.qbox.sdk.domain.model.upload.UploadResult

fun UploadResponse.toUploadResult(): UploadResult =
    UploadResult(hash = hash, title = title, url = url)
