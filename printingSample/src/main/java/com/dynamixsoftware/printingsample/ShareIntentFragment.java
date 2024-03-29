package com.dynamixsoftware.printingsample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ShareIntentFragment extends Fragment implements View.OnClickListener {

    private static final String PRINT_HAND_FREEMIUM = "com.dynamixsoftware.printhand";
    private static final String PRINT_HAND_PREMIUM = "com.dynamixsoftware.printhand.premium";

    private static final int REQUEST_CODE_IMAGE = 1101;

    private static final int REQUEST_CODE_LICENSE = 1100;
    public final static int RESULT_ACTIVATION_SUCCESS = 2766;
    public final static int RESULT_ACTIVATION_ERROR = 2989;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_share_intent, container, false);
        root.findViewById(R.id.share_image_action_view).setOnClickListener(this);
        root.findViewById(R.id.share_image_action_send).setOnClickListener(this);
        root.findViewById(R.id.share_image_multiple).setOnClickListener(this);
        root.findViewById(R.id.share_image_return).setOnClickListener(this);
        root.findViewById(R.id.share_file).setOnClickListener(this);
        root.findViewById(R.id.share_web_page_uri).setOnClickListener(this);
        root.findViewById(R.id.share_web_page_string).setOnClickListener(this);
        root.findViewById(R.id.activate_license).setOnClickListener(this);
        root.findViewById(R.id.activate_license_return).setOnClickListener(this);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LICENSE) {
            switch (resultCode) {
                case RESULT_ACTIVATION_SUCCESS:
                    showDialog(R.string.success, R.string.message_success_license_activation);
                    break;
                case RESULT_ACTIVATION_ERROR:
                    showDialog(R.string.error, R.string.message_error_license_activation);
                    break;
            }
        }
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_CANCELED)
            showDialog(R.string.cancelled, R.string.message_share_image_cancelled);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_image_action_view: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(FilesUtils.getFileUriWithPermission(requireContext(), FilesUtils.FILE_PNG));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (startPrintHandActivityFailed(intent))
                    showStartPrintHandActivityErrorDialog();
                break;
            }
            case R.id.share_image_action_send: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, FilesUtils.getFileUriWithPermission(requireContext(), FilesUtils.FILE_PNG));
                intent.setType("image/*");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (startPrintHandActivityFailed(intent))
                    showStartPrintHandActivityErrorDialog();
                break;
            }
            case R.id.share_image_multiple: {
                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                Uri uri = FilesUtils.getFileUriWithPermission(requireContext(), FilesUtils.FILE_PNG);
                ArrayList<Uri> urisList = new ArrayList<>();
                urisList.add(uri);
                urisList.add(uri);
                urisList.add(uri);
                intent.putExtra(Intent.EXTRA_STREAM, urisList);
                intent.setType("image/*");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (startPrintHandActivityFailed(intent))
                    showStartPrintHandActivityErrorDialog();
                break;


            }
            case R.id.share_image_return: {
                Intent intent = new Intent(Intent.ACTION_VIEW); // can be ACTION_SEND - see share_image_action_send for properly configure intent
                intent.setData(FilesUtils.getFileUriWithPermission(requireContext(), FilesUtils.FILE_PNG));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (startPrintHandActivityForResultFailed(intent, REQUEST_CODE_IMAGE))
                    showStartPrintHandActivityErrorDialog();
                break;
            }
            case R.id.share_file: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(FilesUtils.getFileUriWithPermission(requireContext(), FilesUtils.FILE_DOC));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (startPrintHandActivityFailed(intent))
                    showStartPrintHandActivityErrorDialog();
                break;
            }
            case R.id.share_web_page_uri: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("http://printhand.com"), "text/html"); // worked only 'http' - bug in PrintHand?
                if (startPrintHandActivityFailed(intent))
                    showStartPrintHandActivityErrorDialog();
                break;
            }
            case R.id.share_web_page_string: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_TEXT, "https://printhand.com");
                if (startPrintHandActivityFailed(intent))
                    showStartPrintHandActivityErrorDialog();
                break;
            }
            case R.id.activate_license: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "YOUR_KEY_HERE"); // put your activation key here
                intent.setType("text/license");
                intent.putExtra("showErrorMessage", true); // if true PrintHand shown error message
                intent.putExtra("return", false); // if true (and showErrorMessage is false) PrintHand provide result to your onActivityResult
                if (startPrintHandActivityFailed(intent))
                    showStartPrintHandActivityErrorDialog();
                break;
            }
            case R.id.activate_license_return: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "YOUR_KEY_HERE"); // put your activation key here
                intent.setType("text/license");
                intent.putExtra("showErrorMessage", false); // if true PrintHand shown error message
                intent.putExtra("return", true); // if true (and showErrorMessage is false) PrintHand provide result to your onActivityResult
                if (startPrintHandActivityForResultFailed(intent, REQUEST_CODE_LICENSE))
                    showStartPrintHandActivityErrorDialog();
                break;
            }
        }
    }

    private boolean startPrintHandActivityFailed(Intent intent) {
        return startActivityFailed(intent, PRINT_HAND_FREEMIUM)
                && startActivityFailed(intent, PRINT_HAND_PREMIUM);
    }

    private boolean startActivityFailed(Intent intent, String packageName) {
        return startActivityForResultFailed(intent, packageName, -1);
    }

    private boolean startPrintHandActivityForResultFailed(Intent intent, int requestCode) {
        return startActivityForResultFailed(intent, PRINT_HAND_FREEMIUM, requestCode)
                && startActivityForResultFailed(intent, PRINT_HAND_PREMIUM, requestCode);
    }

    private boolean startActivityForResultFailed(Intent intent, String packageName, int requestCode) {
        try {
            intent.setPackage(packageName);
            startActivityForResult(intent, requestCode);
            return false;
        } catch (ActivityNotFoundException e) {
            return true;
        }
    }

    private void showStartPrintHandActivityErrorDialog() {
        showDialog(R.string.error, R.string.message_error_start_intent);
    }

    private void showDialog(int title, int message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}
