package com.example.graduation.wordbookp2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;


public class WbShareAdapter extends RecyclerView.Adapter<WbShareAdapter.WbShareViewHolder>{
    private Context mContext;
    private List<WbShareUploadDB> mUploads;

    private OnItemClickListener mListener;


    public WbShareAdapter(Context context, List<WbShareUploadDB> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public WbShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.wb_share_item, parent, false);
        return new WbShareViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WbShareViewHolder holder, int position) {
        WbShareUploadDB uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        holder.textViewKind.setText(uploadCurrent.getTextKind());
        holder.textViewExplain.setText(uploadCurrent.getTextExplain());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    //아이템 보여주는 부분
    public class WbShareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener{
        public TextView textViewName;
        public TextView textViewKind;
        public TextView textViewExplain;


        public WbShareViewHolder(View itemView){
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewKind = itemView.findViewById(R.id.text_kind);
            textViewExplain = itemView.findViewById(R.id.text_explain);

            itemView.setOnClickListener(this);//클릭 리스너 설정
            itemView.setOnCreateContextMenuListener(this);//컨텍스트 메뉴 리스너
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position); // 클릭한 아이템의 이벤트
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem download = menu.add(Menu.NONE, 2, 2, "Download");//다운로드
            MenuItem delete = menu.add(Menu.NONE, 3, 3, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            download.setOnMenuItemClickListener(this);//다운로드
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDownloadClick(position);
                            return true;
                        case 3:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener { //이미지 액티비티로
        void onItemClick(int position);
        void onWhatEverClick(int position);
        void onDownloadClick(int position);//다운로드
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}