package hq.remview.ui.main.store;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.MenuRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import hq.remview.BR;
import hq.remview.R;
import hq.remview.data.model.api.request.VerifyTokenRequest;
import hq.remview.data.model.api.request.WebQRCodeRequest;
import hq.remview.data.model.db.RestaurantEntity;
import hq.remview.databinding.DialogUpdateRestaurantNameBinding;
import hq.remview.databinding.FragmentStoreBinding;
import hq.remview.databinding.LayoutActiveRestaurantBinding;
import hq.remview.di.component.FragmentComponent;
import hq.remview.ui.base.adapter.OnItemClickListener;
import hq.remview.ui.base.fragment.BaseFragment;
import hq.remview.ui.main.MainActivity;
import hq.remview.ui.main.adapter.MainAdapter;
import hq.remview.ui.main.adapter.MainItem;
import hq.remview.ui.main.scanner.CustomScanner;
import timber.log.Timber;

public class StoreFragment extends BaseFragment<FragmentStoreBinding,StoreViewModel>
        implements View.OnClickListener{

    private static final int REQUEST_OPEN_SCAN_QRCODE = 2309;
    private static final int REQUEST_OPEN_SCAN_QRCODE_FROM_PC = 2139;
    private List<MainItem> mainItems = new ArrayList<>();
    private MainAdapter mainAdapter;
    private int scanType;

    public StoreFragment(){
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_store;
    }

    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);


        mainAdapter = new MainAdapter(mainItems);
        mainAdapter.setClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                viewModel.currentRestaurant = mainItems.get(position).getEntity();
                requireActivity().runOnUiThread(() -> {
                    ((MainActivity) requireActivity()).verifyToken(viewModel.currentRestaurant);
                });
            }

            @Override
            public void onItemDelete(int position) {
                showWarningDelete(position);
            }
        });
        binding.restaurantRecyclerView.setAdapter(mainAdapter);

        // init recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        binding.restaurantRecyclerView.setLayoutManager(linearLayoutManager);
        viewModel.restaurantEntityLiveData.observe(this, restaurantEntities -> {
            mainItems.clear();
            for (RestaurantEntity entity : restaurantEntities) {
                mainItems.add(new MainItem(entity));
            }
            if (mainItems.isEmpty()) {
//                binding.withoutRestaurant.setVisibility(View.VISIBLE);
//                binding.withRestaurant.setVisibility(View.GONE);
//                openScanQRCode(REQUEST_OPEN_SCAN_QRCODE);

            } else {
//                binding.withoutRestaurant.setVisibility(View.GONE);
//                binding.withRestaurant.setVisibility(View.VISIBLE);
            }
            mainAdapter.notifyDataSetChanged();
        });
        updateFlag();

    }
    private void updateFlag(){
        String currentLang = viewModel.getLang();
        switch (currentLang) {
            case "vi":
                binding.header.setFlag(R.drawable.vietnam);
                break;
            case "en":
                binding.header.setFlag(R.drawable.great_britain);
                break;
            case "de":
                binding.header.setFlag(R.drawable.germany);
                break;
            default:
                break;
        }
        binding.header.executePendingBindings();
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
    private void showWarningDelete(final int pos) {
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.main_dialog_title)
                .setMessage(R.string.main_dialog_message)
                .setPositiveButton(R.string.main_dialog_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        viewModel.deleteRestaurant(mainItems.get(pos).getEntity());
                        mainAdapter.current = null;
                    }
                })
                .setNegativeButton(R.string.main_dialog_cancel, null)
                .show();
    }

    public void openScanQRCode(int requestOpenScanQrcode) {
        scanType = requestOpenScanQrcode;
        ScanOptions integrator = new ScanOptions();
        integrator.setCaptureActivity(CustomScanner.class);
        integrator.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setPrompt("");
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        scan.launch(integrator);
    }
    private final ActivityResultLauncher<ScanOptions> scan = registerForActivityResult(new ScanContract(), result -> {
        if (scanType == REQUEST_OPEN_SCAN_QRCODE) {
            Timber.d("Handle request from mobile");
            if (result.getOriginalIntent()!=null && result.getOriginalIntent().getExtras() != null && result.getOriginalIntent().getExtras().getBoolean("gallery")) {
                scanQrCodeFromGallery();
            } else {
                handleQRCode(result.getContents());
            }
        } else if (scanType == REQUEST_OPEN_SCAN_QRCODE_FROM_PC) {
            Timber.d("Handle request from pc");
            handleQRCodeFromPC(result.getContents());
        }

});

    private void handleQRCodeFromPC(String result){
        viewModel.showSuccessMessage(getString(R.string.scan_qr_success));
        sendQRCodeToWebView(result);
    }
    private void sendQRCodeToWebView(String qrCode ){
        Timber.d("Send to qrcode pc: %s", qrCode);
        WebQRCodeRequest webQRCodeRequest = new WebQRCodeRequest();
        webQRCodeRequest.setQrCode(qrCode);
        }
    private void handleQRCode(String result){
        if (result == null) {
            viewModel.showErrorMessage(getString(R.string.scan_qr_code_cancelled));
//            openScanQRCode(REQUEST_OPEN_SCAN_QRCODE);
        } else {
            try {
                viewModel.currentRestaurant = new RestaurantEntity();
                viewModel.currentRestaurant.setLastAccessDate(new Date());
                viewModel.currentRestaurant.setName("");
                viewModel.currentRestaurant.setId(String.valueOf(System.currentTimeMillis()));
                doVerifyQRCode(result);
            } catch (IndexOutOfBoundsException e) {
                viewModel.showErrorMessage(getString(R.string.qr_code_wrong_format));
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_app_bar_qr:
            case R.id.scan_qr_code:
                scanQrCode();
                break;
            case R.id.main_app_bar_calendar:
                mainAdapter.closeCurrent();
                Timber.d("Show calendar");
                break;

            case R.id.restaurant_rename_cancel:
                alertDialog.dismiss();
                break;
            case R.id.restaurant_rename_confirm:
                updateRestaurantName();
                break;
            case R.id.restaurant_check_cancel:
                checkDialog.dismiss();
                break;
            case R.id.restaurant_check_confirm:
                ((MainActivity) requireActivity()).verifyToken(viewModel.currentRestaurant);
                checkDialog.dismiss();
                break;
            case R.id.settings:
                showMenu(v,R.menu.language_menu);
                break;
            default:
                break;
        }
    }
    @SuppressLint("RestrictedApi")
    private void showMenu(View view, @MenuRes Integer menuRes){
        PopupMenu popupMenu = new PopupMenu(requireContext(),view);
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());
        if (popupMenu.getMenu() instanceof MenuBuilder){
            MenuBuilder menuBuilder = (MenuBuilder) popupMenu.getMenu();
            menuBuilder.setOptionalIconsVisible(true);
            for (MenuItemImpl item : menuBuilder.getVisibleItems()) {
                if (item.getIcon()!=null){
                    Integer iconMarginPx = 0;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        item.setIcon(new InsetDrawable(item.getIcon(), iconMarginPx, 0, iconMarginPx,0));
                    } else {
                        item.setIcon(
                                new InsetDrawable(item.getIcon(), iconMarginPx, 0, iconMarginPx, 0) {
                                    @Override
                                    public int getIntrinsicWidth() {
                                        return getIntrinsicHeight() + iconMarginPx + iconMarginPx;
                                    }
                                });
                    }
                }
            }
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.vi:
                        setLocale("vi");
                        break;
                    case R.id.en:
                        setLocale("en");
                        break;
                    case R.id.ger:
                        setLocale("de");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }
    public void setLocale(String languageCode) {
        if (languageCode.equals(viewModel.getLang())) return;
        viewModel.updateLang(languageCode);
        updateFlag();
        ((MainActivity)requireActivity()).recreateScreen();
    }


    private void updateRestaurantName(){
        if (!Objects.equals(viewModel.restaurantName.get(), "")){
            viewModel.currentRestaurant.setName(viewModel.restaurantName.get());
            viewModel.addRestaurant(viewModel.currentRestaurant);
            alertDialog.dismiss();
            showCheckDialog();
        } else {
            requireActivity().runOnUiThread(()->viewModel.showErrorMessage(getString(R.string.restaurant_empty_name)));
        }
    }

    private void scanQrCode() {
        mainAdapter.closeCurrent();
        openScanQRCode(REQUEST_OPEN_SCAN_QRCODE);
    }

    private final ActivityResultLauncher<Intent> galleryI = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if  (result.getData() == null){
            return;
        }
        Uri uri = result.getData().getData();
        try
        {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null)
            {
                Log.e("TAG", "uri is not a bitmap," + uri.toString());
                return;
            }
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle();
            bitmap = null;
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
            try
            {
                Result resultX = reader.decode(bBitmap);
                handleQRCode(resultX.getText());
            }
            catch (NotFoundException e)
            {
                viewModel.showErrorMessage(getString(R.string.qr_code_not_found_gallery));
            }
        }
        catch (FileNotFoundException e)
        {
            viewModel.showErrorMessage(getString(R.string.gallery_open_error));
        }
    });

    private void scanQrCodeFromGallery(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType( android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        galleryI.launch(pickIntent);
    }

    private void doVerifyQRCode(String message) {
        Timber.d("Verify QR Code");
        ((MainActivity)requireActivity()).verifyQRCode(message);
    }

    private void verifyToken() {
        Timber.d("Verify token");
        VerifyTokenRequest verifyTokenRequest = new VerifyTokenRequest();
        verifyTokenRequest.setToken(viewModel.currentRestaurant.getToken());
        Timber.d("request: "+verifyTokenRequest.toString());
    }

    AlertDialog alertDialog;
    public void showUpdateRestaurantNameDialog(String posName,String id,boolean isActive){
        viewModel.restaurantName.set(posName);
        viewModel.currentRestaurant.setPosId(id);
        viewModel.currentRestaurant.active = isActive;
        if (checkPosId()) return;

        DialogUpdateRestaurantNameBinding restaurantNameBinding = DialogUpdateRestaurantNameBinding.inflate(requireActivity().getLayoutInflater());
        restaurantNameBinding.setParent(this);
        restaurantNameBinding.setName(viewModel.restaurantName);
        alertDialog = new AlertDialog.Builder(requireContext())
                .setView(restaurantNameBinding.getRoot())
                .create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();
    }
    private boolean checkPosId(){
        return viewModel.restaurantEntityLiveData.getValue().stream().anyMatch(s->s.posId.equals(viewModel.currentRestaurant.posId));
    }
    AlertDialog checkDialog;
    public void showCheckDialog(){
        if (viewModel.currentRestaurant.isActive()) return;
        LayoutActiveRestaurantBinding binding = LayoutActiveRestaurantBinding.inflate(requireActivity().getLayoutInflater());
        binding.setParent(this);
        checkDialog = new AlertDialog.Builder(requireContext())
                .setView(binding.getRoot())
                .create();
        checkDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        checkDialog.show();
    }
    public void addRestaurant(String session){
        viewModel.currentRestaurant.setToken(session);
    }

    @androidx.databinding.BindingAdapter("load_flag")
    public static void glideImage(ImageView view,@DrawableRes Integer drawable) {
        view.setImageDrawable(ContextCompat.getDrawable(view.getContext(),drawable));
    }
}
