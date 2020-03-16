package ptv.example.zoulinheng.androidutils.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ptv.example.zoulinheng.androidutils.R;
import ptv.example.zoulinheng.androidutils.utils.helpers.PermissionHelper;
import ptv.example.zoulinheng.androidutils.utils.permissions.PermissionsPageManager;

import static ptv.example.zoulinheng.androidutils.widgets.PermissionPopupWindow.MODE.MODE_NON;
import static ptv.example.zoulinheng.androidutils.widgets.PermissionPopupWindow.MODE.MODE_TO_SETTING;
import static ptv.example.zoulinheng.androidutils.widgets.PermissionPopupWindow.OPERATION.OPERATION_CLOSE;
import static ptv.example.zoulinheng.androidutils.widgets.PermissionPopupWindow.OPERATION.OPERATION_OK;

/**
 * Created by lhZou on 2017/11/23.
 * desc:权限提示窗口
 */
public class PermissionPopupWindow extends PopupWindow {
    public static final int RESULT_CODE_SUCCESS = 0x99;
    public static final int RESULT_CODE_FAIL = 0x98;
    public static final int RESULT_CODE_QUIT = 0x97;

    private int resultCode;

    private View contentView;

    private ViewHolder holder;

    private String[] permissions;
    private String[] permissionNames;
    private @MODE
    int mode;//1:只提示  其他:跳转到权限设置页面
    private @OPERATION
    int operation;

    private Map<String, String> map = new HashMap<>();

    public interface PopupDismissListener {
        void onDismiss(@MODE int mode, @OPERATION int operation, int resultCode);
    }

    public PermissionPopupWindow(final Activity activity, String[] permissions, @MODE int mode, final PopupDismissListener dismissListener) {
        this.mContext = activity;
        this.permissions = permissions;
        this.mode = mode;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.activity_open_permission, null);
        int h = activity.getWindowManager().getDefaultDisplay().getHeight();
        int w = activity.getWindowManager().getDefaultDisplay().getWidth();
        this.setContentView(contentView);
        this.setWidth(5 * w / 6 + 50);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.shape_bg_frame_primary_permission));
        // 刷新状态
        this.update();

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                dismissListener.onDismiss(PermissionPopupWindow.this.mode, operation, resultCode);
            }
        });

        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        activity.getWindow().setAttributes(lp);

        holder = new ViewHolder(contentView);

        permissionNames = PermissionHelper.getPermissionNames(this.permissions);
        for (int i = 0; i < this.permissions.length; i++) {
            map.put(this.permissions[i], permissionNames != null && permissionNames.length != 0 ? permissionNames[i % permissionNames.length] : "");
        }

        holder.gvPermissions.setAdapter(new PermissionAdapter(activity));

        holder.ok.setText(mode == MODE_NON ? "确定" : "去开启");
        holder.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operation = OPERATION_CLOSE;
                resultCode = RESULT_CODE_QUIT;
                dismiss();
            }
        });

        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operation = OPERATION_OK;
                if (PermissionPopupWindow.this.mode == MODE_TO_SETTING) {
                    activity.startActivity(PermissionsPageManager.getIntent(activity));
                    resultCode = 0;
                    dismiss();
                } else if (PermissionPopupWindow.this.mode == MODE_NON) {
                    String[] per = PermissionHelper.getDeniedPermissions(activity, PermissionPopupWindow.this.permissions);
                    List<String> pers = new ArrayList<>();
                    StringBuilder sb = new StringBuilder("");
                    for (String s : per) {
                        if (PermissionHelper.hasAlwaysDeniedPermission(activity, s)) {
                            sb.append(map.get(s));
                            pers.add(s);
                        }
                    }
                    if (sb.toString().trim().length() != 0) {
                        Intent intent = new Intent();
                        intent.putExtra("permissions", pers.toArray(new String[pers.size()]));
                        resultCode = RESULT_CODE_FAIL;
                        dismiss();
                    } else {
                        resultCode = RESULT_CODE_SUCCESS;
                        dismiss();
                    }
                }
            }
        });
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
        } else {
            this.dismiss();
        }
    }

    private Activity mContext;

    public class PermissionAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        private PermissionAdapter.ContentViewHolder holder;

        public PermissionAdapter(Context context) {
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

        public class ContentViewHolder extends RecyclerView.ViewHolder {
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

    @IntDef({MODE_TO_SETTING, MODE_NON})
    public @interface MODE {
        int MODE_TO_SETTING = 0;
        int MODE_NON = 1;
    }

    @IntDef({OPERATION_CLOSE, OPERATION_OK})
    public @interface OPERATION {
        int OPERATION_CLOSE = 0;
        int OPERATION_OK = 1;
    }
}
