package hq.remview.ui.main.setting.permission.edit;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import hq.remview.MVVMApplication;
import hq.remview.data.Repository;
import hq.remview.ui.base.activity.BaseViewModel;

public class PermissionEditViewModel extends BaseViewModel {
    public ObservableField<String> name = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");
    public ObservableField<String> nfc = new ObservableField<>("");
    public ObservableBoolean ibutton = new ObservableBoolean(false);
    public PermissionEditViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
}
