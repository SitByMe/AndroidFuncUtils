package ptv.example.zoulinheng.androidutils.widgets;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.IntDef;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;

import ptv.example.zoulinheng.androidutils.R;
import ptv.example.zoulinheng.androidutils.helper.PermissionHelper;

/**
 * Created by zoulinheng on 2017/11/23.
 * desc:请求权限结果提示窗口
 */
public class PermissionPopupWindow extends PopupWindow {
    private @PermissionConstants.Permission
    String[] permissionNames;
    private @OPERATION
    int operation;
    private @CLOSE_RESULT
    int closeResult;

    private Activity activity;

    public interface PopupDismissListener {
        void onDismiss(boolean mForce, @OPERATION int operation, @CLOSE_RESULT int closeResult);
    }

    /**
     * @param force 是否强制跳转到权限设置页面
     */
    public PermissionPopupWindow(final Activity activity, @PermissionConstants.Permission String[] permissions, final boolean force, final PopupDismissListener dismissListener) {
        this.activity = activity;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_open_permission, null);
        int h = activity.getWindowManager().getDefaultDisplay().getHeight();
        int w = activity.getWindowManager().getDefaultDisplay().getWidth();
        this.setContentView(contentView);
        this.setWidth(5 * w / 6 + 50);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.shape_bg_frame_primary_permission));
        // 刷新状态
        this.update();

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                dismissListener.onDismiss(force, operation, closeResult);
            }
        });

        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        activity.getWindow().setAttributes(lp);

        ViewHolder holder = new ViewHolder(contentView);

        permissionNames = PermissionHelper.getPermissionNames(permissions);

        holder.gvPermissions.setAdapter(new PermissionAdapter(activity));

        holder.ok.setText("去开启");
        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operation = OPERATION.OPERATION_OK;
                closeResult = CLOSE_RESULT.CLOSE_OPTION_TO_SETTING_ACT;
                dismiss();
            }
        });

        holder.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operation = OPERATION.OPERATION_CLOSE;
                if (force) {
                    closeResult = CLOSE_RESULT.CLOSE_OPTION_REQUEST_AGAIN;
                } else {
                    closeResult = CLOSE_RESULT.CLOSE_OPTION_NON;
                }
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 1.0f;
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public class PermissionAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        PermissionAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return permissionNames.length;
        }

        @Override
        public Object getItem(int position) {
            return permissionNames[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ContentViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_permissions_notice, null);
                holder = new PermissionAdapter.ContentViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (PermissionAdapter.ContentViewHolder) convertView.getTag();
            }

            holder.tvPermissionName.setText(permissionNames[position]);
            return convertView;
        }

        class ContentViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvPermissionName;

            ContentViewHolder(View view) {
                super(view);
                ivIcon = view.findViewById(R.id.iv_icon);
                tvPermissionName = view.findViewById(R.id.tv_permission_name);
            }
        }
    }

    static class ViewHolder {
        GridView gvPermissions;
        AppCompatTextView ok;
        ImageView ivClose;

        ViewHolder(View view) {
            gvPermissions = view.findViewById(R.id.gv_permissions);
            ok = view.findViewById(R.id.ok);
            ivClose = view.findViewById(R.id.iv_close);
        }
    }

    @IntDef({OPERATION.OPERATION_CLOSE, OPERATION.OPERATION_OK})
    public @interface OPERATION {
        int OPERATION_CLOSE = 0;
        int OPERATION_OK = 1;
    }

    @IntDef({CLOSE_RESULT.CLOSE_OPTION_REQUEST_AGAIN,
            CLOSE_RESULT.CLOSE_OPTION_TO_SETTING_ACT,
            CLOSE_RESULT.CLOSE_OPTION_NON,
            CLOSE_RESULT.CLOSE_OPTION_EXIT})
    public @interface CLOSE_RESULT {
        int CLOSE_OPTION_REQUEST_AGAIN = 0x99;
        int CLOSE_OPTION_TO_SETTING_ACT = 0x98;
        int CLOSE_OPTION_NON = 0x97;
        int CLOSE_OPTION_EXIT = 0x96;
    }
}
