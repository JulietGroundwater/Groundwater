package uk.ac.cam.cl.juliet.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import uk.ac.cam.cl.juliet.R;
import uk.ac.cam.cl.juliet.models.SingleOrManyBursts;

/**
 * Adapts a list of files into the RecyclerView in DataFragment.
 *
 * @author Ben Cole
 */
public class FilesListAdapter extends RecyclerView.Adapter<FilesListAdapter.FilesListViewHolder> {

    private List<SingleOrManyBursts> dataset;
    private OnDataFileSelectedListener listener;

    /**
     * Must be implemented by the containing Fragment (or Activity) so that a dialog can be
     * displayed when a file is selected.
     */
    public interface OnDataFileSelectedListener {

        /**
         * Invoked when a file or folder in the list is clicked.
         *
         * @param itemClicked The burst or collection of bursts that was clicked
         * @param viewHolder The ViewHolder containing all UI elements in that row
         */
        void onDataFileClicked(SingleOrManyBursts itemClicked, FilesListViewHolder viewHolder);

        /**
         * Invoked when a file or folder in the list is long clicked.
         *
         * @param itemClicked The burst or collection of bursts that was long clicked
         * @param viewHolder The ViewHolder containing all UI elements in that row
         * @return true if the long click was consumed; false otherwise
         */
        boolean onDataFileLongClicked(
                SingleOrManyBursts itemClicked, FilesListViewHolder viewHolder);
    }

    public FilesListAdapter(List<SingleOrManyBursts> files) {
        super();
        dataset = files;
    }

    /**
     * Sets the listener for when a data file is selected.
     *
     * @param listener The OnDataFileSelectedListener to use
     */
    public void setOnDataFileSelectedListener(OnDataFileSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.listelement_data_file, parent, false);
        return new FilesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FilesListViewHolder filesListViewHolder, int i) {
        final SingleOrManyBursts file = dataset.get(i);
        filesListViewHolder.getTimestampTextView().setText(file.getNameToDisplay());
        filesListViewHolder.getUploadingSpinner().setVisibility(View.INVISIBLE);
        if (file.getIsSingleBurst()) {
            filesListViewHolder
                    .getTypeImageView()
                    .setImageResource(R.drawable.baseline_show_chart_black_36);
        } else {
            filesListViewHolder
                    .getTypeImageView()
                    .setImageResource(R.drawable.baseline_folder_black_36);
        }
        if (file.getSyncStatus()) {
            filesListViewHolder
                    .getSyncStatusImageView()
                    .setImageResource(R.drawable.baseline_cloud_done_black_18);
        } else {
            filesListViewHolder
                    .getSyncStatusImageView()
                    .setImageResource(R.drawable.baseline_cloud_off_black_18);
        }
        filesListViewHolder.setSpinnerVisibility(false);
        filesListViewHolder
                .getContainer()
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onDataFileClicked(file, filesListViewHolder);
                            }
                        });
        filesListViewHolder
                .getContainer()
                .setOnLongClickListener(
                        new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return listener.onDataFileLongClicked(file, filesListViewHolder);
                            }
                        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /**
     * Contains references to all Views to prevent unnecessary DOM parsing when rows are recycled.
     */
    public static class FilesListViewHolder extends RecyclerView.ViewHolder {

        private View container;
        private TextView timestampTextView;
        private ImageView typeImageView;
        private ImageView syncStatusImageView;
        private ProgressBar uploadingSpinner;

        FilesListViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView;
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            typeImageView = itemView.findViewById(R.id.typeIcon);
            syncStatusImageView = itemView.findViewById(R.id.syncStatusImageView);
            uploadingSpinner = itemView.findViewById(R.id.uploadingSpinner);
        }

        /**
         * Sets the visibility of the progress spinner.
         *
         * @param visible true to make the spinner visible; false to make it invisible
         */
        public void setSpinnerVisibility(boolean visible) {
            uploadingSpinner.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }

        /**
         * Sets the visibility of the sync status icon.
         *
         * @param visible true to make the icon visible; false to make it invisble
         */
        public void setSyncStatusVisibility(boolean visible) {
            syncStatusImageView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }

        public View getContainer() {
            return container;
        }

        public TextView getTimestampTextView() {
            return timestampTextView;
        }

        public ImageView getTypeImageView() {
            return typeImageView;
        }

        public ImageView getSyncStatusImageView() {
            return syncStatusImageView;
        }

        public ProgressBar getUploadingSpinner() {
            return uploadingSpinner;
        }
    }
}
