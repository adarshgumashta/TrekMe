package com.peterlaurence.trekadvisor.menu.maplist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.peterlaurence.trekadvisor.R;

/**
 * A {@link Dialog} that shows the progression of a map download.
 *
 * @author peterLaurence on 07/10/17.
 */
public class MapDownloadDialog extends DialogFragment {
    private static final String MAP_NAME = "map_name";
    private String mTitle;
    private ProgressBar mProgressBar;

    public static MapDownloadDialog newInstance(String mapName) {
        MapDownloadDialog frag = new MapDownloadDialog();
        Bundle args = new Bundle();
        args.putString(MAP_NAME, mapName);
        frag.setArguments(args);
        return frag;
    }

    public void setProgress(int p) {
        mProgressBar.setProgress(p);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mTitle = context.getString(R.string.download_map_dialog_title);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        View view = getActivity().getLayoutInflater().inflate(R.layout.download_map_dialog, null);

        TextView msg = (TextView) view.findViewById(R.id.download_map_dialog_msg);
        String mMapName = getArguments().getString(MAP_NAME);
        msg.setText(String.format(getString(R.string.download_map_dialog_msg), mMapName));
        mProgressBar = (ProgressBar) view.findViewById(R.id.download_map_dialog_progress);

        builder.setView(view);
        builder.setNegativeButton(getString(R.string.cancel_dialog_string), null);
        builder.setCancelable(false);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
