package com.akamaidev.urbancrawlapp.helpers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

/*
 * Copyright 2017 Akamai Technologies, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface OnDateSetListener{
        public void onDateSet(int year, int month, int dayOfMonth, String dialogType);
    }

    static String KEY_DAY_OF_MONTH = "KEY_DAY_OF_MONTH";
    static String KEY_MONTH = "KEY_MONTH";
    static String KEY_YEAR = "KEY_YEAR";
    static String KEY_DIALOG_TYPE = "KEY_DIALOG_TYPE";

    public static String VAL_DIALOG_TYPE_START_DATE = "VAL_DIALOG_TYPE_START_DATE";
    public static String VAL_DIALOG_TYPE_END_DATE = "VAL_DIALOG_TYPE_END_DATE";

    OnDateSetListener callback;

    String currentDialogType;

    public DatePickerFragment(){
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        int dayOfMOnth = getArguments().getInt(KEY_DAY_OF_MONTH);
        int month = getArguments().getInt(KEY_MONTH);
        int year = getArguments().getInt(KEY_YEAR);
        currentDialogType = getArguments().getString(KEY_DIALOG_TYPE);

        return new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, year, month, dayOfMOnth);
    }


    public static DatePickerFragment getInstance(OnDateSetListener callback, int dayOfMonth, int month, int year, String dialogType){
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.callback = callback;

        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putInt(KEY_DAY_OF_MONTH, dayOfMonth);
        fragmentArgs.putInt(KEY_MONTH, month);
        fragmentArgs.putInt(KEY_YEAR, year);
        fragmentArgs.putString(KEY_DIALOG_TYPE, dialogType);

        datePickerFragment.setArguments(fragmentArgs);
        return datePickerFragment;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        callback.onDateSet(year, month+1, dayOfMonth, currentDialogType);
    }
}
