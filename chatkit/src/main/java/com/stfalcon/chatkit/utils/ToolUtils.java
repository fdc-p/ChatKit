package com.stfalcon.chatkit.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ToolUtils {
    public static void copyToClipboard(Context context, String copiedText) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(copiedText, copiedText);
        clipboard.setPrimaryClip(clip);
    }
}
