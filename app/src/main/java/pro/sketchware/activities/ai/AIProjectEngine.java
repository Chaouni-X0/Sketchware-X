package pro.sketchware.activities.ai;

import android.content.Context;
import com.besome.sketch.beans.BlockBean;
import com.besome.sketch.beans.ViewPropertyBean;
import com.besome.sketch.beans.ViewBean;
import com.besome.sketch.beans.ProjectLibraryBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import a.a.a.FileUtil;

public class AIProjectEngine {

    private Context context;
    private Gson gson;

    public AIProjectEngine(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public void processAIResponse(String jsonResponse, String projectPath) {
        try {
            Map<String, Object> responseMap = gson.fromJson(jsonResponse, new TypeToken<Map<String, Object>>(){}.getType());
            
            if (responseMap.containsKey("views")) {
                createViews((List<Map<String, Object>>) responseMap.get("views"), projectPath);
            }
            
            if (responseMap.containsKey("logic")) {
                createLogic((List<Map<String, Object>>) responseMap.get("logic"), projectPath);
            }

            if (responseMap.containsKey("libraries")) {
                updateLibraries((List<Map<String, Object>>) responseMap.get("libraries"), projectPath);
            }

            if (responseMap.containsKey("permissions")) {
                updatePermissions((List<String>) responseMap.get("permissions"), projectPath);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createViews(List<Map<String, Object>> viewsList, String projectPath) {
        ArrayList<ViewBean> viewBeans = new ArrayList<>();
        for (Map<String, Object> viewData : viewsList) {
            String id = (String) viewData.get("id");
            String typeStr = (String) viewData.get("type");
            int type = ViewBean.getViewTypeByTypeName(typeStr);
            ViewBean bean = new ViewBean(id, type);
            if (viewData.containsKey("text")) bean.text.text = (String) viewData.get("text");
            if (viewData.containsKey("attributes")) {
                applyViewAttributes(bean, (Map<String, Object>) viewData.get("attributes"));
            }
            viewBeans.add(bean);
        }
        FileUtil.writeFile(projectPath + "/main.view", gson.toJson(viewBeans));
    }

    private void applyViewAttributes(ViewBean bean, Map<String, Object> attributes) {
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (key) {
                case "layout_width":
                    bean.layout.width = convertLayoutParam((String) value);
                    break;
                case "layout_height":
                    bean.layout.height = convertLayoutParam((String) value);
                    break;
                case "text_color":
                    bean.text.color = (String) value;
                    break;
                case "background_color":
                    bean.background.color = (String) value;
                    break;
                case "padding":
                    // Assuming padding is a single value for all sides for simplicity
                    int padding = convertDpToPx((String) value);
                    bean.padding.left = padding;
                    bean.padding.top = padding;
                    bean.padding.right = padding;
                    bean.padding.bottom = padding;
                    break;
                case "margin_left":
                    bean.margin.left = convertDpToPx((String) value);
                    break;
                case "margin_top":
                    bean.margin.top = convertDpToPx((String) value);
                    break;
                case "margin_right":
                    bean.margin.right = convertDpToPx((String) value);
                    break;
                case "margin_bottom":
                    bean.margin.bottom = convertDpToPx((String) value);
                    break;
                // Add more attributes as needed
            }
        }
    }

    private int convertLayoutParam(String value) {
        if (value.equals("match_parent")) {
            return ViewPropertyBean.MATCH_PARENT;
        } else if (value.equals("wrap_content")) {
            return ViewPropertyBean.WRAP_CONTENT;
        } else if (value.endsWith("dp")) {
            return convertDpToPx(value);
        }
        return 0; // Default or error
    }

    private int convertDpToPx(String dpValue) {
        try {
            float dp = Float.parseFloat(dpValue.replace("dp", ""));
            return (int) (dp * context.getResources().getDisplayMetrics().density);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void createLogic(List<Map<String, Object>> logicList, String projectPath) {
        ArrayList<BlockBean> blockBeans = new ArrayList<>();
        for (Map<String, Object> logicData : logicList) {
            List<Map<String, Object>> blocks = (List<Map<String, Object>>) logicData.get("blocks");
            for (Map<String, Object> blockData : blocks) {
                BlockBean bean = new BlockBean("0", (String) blockData.get("spec"), (String) blockData.get("type"), "none");
                blockBeans.add(bean);
            }
        }
        FileUtil.writeFile(projectPath + "/main.logic", gson.toJson(blockBeans));
    }

    private void updateLibraries(List<Map<String, Object>> librariesList, String projectPath) {
        ArrayList<ProjectLibraryBean> libraryBeans = new ArrayList<>();
        for (Map<String, Object> libData : librariesList) {
            int type = ((Double) libData.get("type")).intValue();
            ProjectLibraryBean bean = new ProjectLibraryBean(type);
            bean.useYn = ProjectLibraryBean.LIB_USE_Y;
            libraryBeans.add(bean);
        }
        FileUtil.writeFile(projectPath + "/project.lib", gson.toJson(libraryBeans));
    }

    private void updatePermissions(List<String> permissions, String projectPath) {
        FileUtil.writeFile(projectPath + "/project.permissions", gson.toJson(permissions));
    }
}
