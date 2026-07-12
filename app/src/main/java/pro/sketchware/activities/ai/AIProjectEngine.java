package pro.sketchware.activities.ai;

import android.content.Context;
import com.besome.sketch.beans.BlockBean;
import com.besome.sketch.beans.ViewBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                List<Map<String, Object>> viewsList = (List<Map<String, Object>>) responseMap.get("views");
                createViews(viewsList, projectPath);
            }
            
            if (responseMap.containsKey("logic")) {
                List<Map<String, Object>> logicList = (List<Map<String, Object>>) responseMap.get("logic");
                createLogic(logicList, projectPath);
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
            if (viewData.containsKey("text")) {
                bean.text.text = (String) viewData.get("text");
            }
            viewBeans.add(bean);
        }
        // هنا يتم حفظ الـ ViewBeans في ملف المشروع .view
        String viewFilePath = projectPath + "/main.view";
        FileUtil.writeFile(viewFilePath, gson.toJson(viewBeans));
    }

    private void createLogic(List<Map<String, Object>> logicList, String projectPath) {
        // منطق مشابه لإنشاء البلوكات وحفظها في ملف .logic
        ArrayList<BlockBean> blockBeans = new ArrayList<>();
        for (Map<String, Object> logicData : logicList) {
            List<Map<String, Object>> blocks = (List<Map<String, Object>>) logicData.get("blocks");
            for (Map<String, Object> blockData : blocks) {
                String type = (String) blockData.get("type");
                String spec = (String) blockData.get("spec");
                BlockBean bean = new BlockBean("0", spec, type, "none");
                blockBeans.add(bean);
            }
        }
        String logicFilePath = projectPath + "/main.logic";
        FileUtil.writeFile(logicFilePath, gson.toJson(blockBeans));
    }
}
