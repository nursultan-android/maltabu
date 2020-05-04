package kz.maltabu.app.maltabukz.di

import com.bumptech.glide.Glide
import kz.maltabu.app.maltabukz.utils.FormatHelper
import kz.maltabu.app.maltabukz.utils.customEnum.EnumsClass
import kz.maltabu.app.maltabukz.utils.web.NetworkChecker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { NetworkChecker(androidContext()) }
    single { FormatHelper() }
    single { EnumsClass() }
    single { Glide.with(androidContext()) }
}