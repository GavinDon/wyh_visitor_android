package com.stxx.wyhvisitorandroid.graphics

import android.app.Activity
import androidx.fragment.app.Fragment
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.PictureWindowAnimationStyle
import com.stxx.wyhvisitorandroid.R
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.SelectionSpec


/**
 * description:
 * Created by liNan on  2020/2/19 23:48
 */

const val REQUEST_CODE_CHOOSE: Int = 0x110

fun Fragment.openAlbum() {
    Matisse.from(this)
        .choose(MimeType.ofImage())
        .countable(true)
        .maxSelectable(9)
        .showSingleMediaType(SelectionSpec.getInstance().onlyShowImages())
        .thumbnailScale(0.85f)
        .imageEngine(PicassoEngine())
        .forResult(REQUEST_CODE_CHOOSE)
}

//矩形裁剪
fun Fragment.gridSinglePicture() {
    PictureSelector.create(this)
        .openGallery(PictureMimeType.ofImage())
        .selectionMode(PictureConfig.SINGLE)
        .isWeChatStyle(false)
        .setPictureWindowAnimationStyle(
            PictureWindowAnimationStyle(
                R.anim.anim_right_in,
                R.anim.anim_right_out
            )
        )
        .loadImageEngine(SelectorGlideEngine.createGlideEngine())
        .theme(R.style.selectPicture)
        .enableCrop(true)
        .circleDimmedLayer(false)
        .compress(true)
        .showCropFrame(true)
        .showCropGrid(true)
        .withAspectRatio(1,1)
        .isDragFrame(false)
        .rotateEnabled(true)
        .freeStyleCropEnabled(false)
        .forResult(REQUEST_CODE_CHOOSE)
}

fun Activity.chooseSinglePicture(isCrop: Boolean = true) {
    PictureSelector.create(this)
        .openGallery(PictureMimeType.ofImage())
        .selectionMode(PictureConfig.SINGLE)
        .isWeChatStyle(false)
        .setPictureWindowAnimationStyle(
            PictureWindowAnimationStyle(
                R.anim.anim_right_in,
                R.anim.anim_right_out
            )
        )
        .isMultipleSkipCrop(false)
        .isSingleDirectReturn(true)
        .circleDimmedLayer(true)
        .loadImageEngine(SelectorGlideEngine.createGlideEngine())
        .theme(R.style.selectPicture)
        .enableCrop(isCrop)
        .compress(true)
        .showCropFrame(false)
        .isDragFrame(true)
        .freeStyleCropEnabled(true)
        .forResult(REQUEST_CODE_CHOOSE)
}

/**
 * 从相册中选择媒体资源(包括图片,视频)
 */
fun Fragment.chooseAlbum(selectionMedia: List<LocalMedia>) {
    PictureSelector.create(this)
        .openGallery(PictureMimeType.ofImage())
        .isWeChatStyle(false)
        .setPictureWindowAnimationStyle(
            PictureWindowAnimationStyle(
                R.anim.anim_right_in,
                R.anim.anim_right_out
            )
        )
        .imageSpanCount(3)
        .maxVideoSelectNum(1)
        .maxSelectNum(3)
        .isWithVideoImage(true)
        .loadImageEngine(SelectorGlideEngine.createGlideEngine())
        .theme(R.style.selectPicture)
        .previewEggs(true)
        .compress(true)
        .previewImage(true)
        .selectionMedia(selectionMedia)
        .forResult(REQUEST_CODE_CHOOSE)
}