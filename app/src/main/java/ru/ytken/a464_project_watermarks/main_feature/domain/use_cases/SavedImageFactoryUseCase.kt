package ru.ytken.a464_project_watermarks.main_feature.domain.use_cases


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.ytken.a464_project_watermarks.main_feature.presentation.image_result.ImageResultFragmentViewModel
import ru.ytken.a464_project_watermarks.main_feature.presentation.new_alg.NewAlgFragmentViewModel
import ru.ytken.a464_project_watermarks.main_feature.presentation.see_scan.SeeScanFragmentViewModel

class SavedImageFactoryUseCase: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImageResultFragmentViewModel::class.java)){
            return ImageResultFragmentViewModel() as T
        }
        if(modelClass.isAssignableFrom(SeeScanFragmentViewModel::class.java)){
            return SeeScanFragmentViewModel() as T
        }
        if(modelClass.isAssignableFrom(NewAlgFragmentViewModel::class.java)){
            return NewAlgFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}