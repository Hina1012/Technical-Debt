package com.company;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Hina Fatima on 9/9/2017.
 */
public class SonarHTTP  extends java.lang.Object {

    dbConnection dbConnectionObj = new dbConnection();
    private List<String> readFile(String filename)
    {
        List<String> records = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null)
            {
                records.add(line);
            }
            reader.close();
            return records;
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }

    class Project {
        private String id;
        private String name;
        private String key;


        public Project(String id, String name, String key) {
            this.id = id;
            this.name = name;
            this.key = key;

        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getKey() {
            return key;
        }
    }

    class Component {

        private String componentId;
        private String componentKey;

        public Component(String componentId, String componentKey) {

            this.componentId=componentId;
            this.componentKey=componentKey;
        }

        public String getComponentId(){
            return componentId;
        }

        public String getComponentKey(){
            return componentKey;
        }

    }

    class Measure {

        private ArrayList<String> metric;
        private ArrayList<String> value;

        public Measure(ArrayList<String> metric, ArrayList<String>value) {

            this.metric=metric;
            this.value=value;
        }

        public ArrayList<String> getMetric(){
            return metric;
        }

        public ArrayList<String> getValue(){
            return value;
        }

    }

    class IssueComponent {

        private String uuid;
        private String project_key;
        private String file_name;
        private String file_path;
        private String component_id;

        public IssueComponent(String uuid, String project_key, String file_name, String file_path, String component_id) {

            this.uuid = uuid;
            this.project_key = project_key;
            this.file_name = file_name;
            this.file_path = file_path;
            this.component_id = component_id;
        }
        public String getUUID() {
            return uuid;
        }

        public String getProjectKey() {
            return project_key;
        }

        public String getFileName() {
            return file_name;
        }

        public String getFilePath() {
            return file_path;
        }

        public String getComponentId() {
            return component_id;
        }
    }

    class Issues {

        private String key;
        private String comp_id;
        private String project;
        private String rule;
        private String line;
        private String text_range;
        private String flows;
        private String status;
        private String message;
        private String author;
        private String tags;
        private String creation_date;
        private String update_date;
        private String severity;
        private String type;


        public Issues(String key, String comp_id, String project, String rule, String line, String text_range, String flows, String status, String message, String author,String tags, String creation_date, String update_date, String severity, String type) {


            this.key=key;
            this.comp_id=comp_id;
            this.project=project;
            this.rule=rule;
            this.line=line;
            this.text_range=text_range;
            this.flows=flows;
            this.status=status;
            this.message=message;
            this.author=author;
            this.tags=tags;
            this.creation_date=creation_date;
            this.update_date=update_date;
            this.severity=severity;
            this.type=type;
        }
        public String geKey() { return key; }

        public String getCompId() { return comp_id; }

        public String getProject() {
            return project;
        }

        public String getRule() {
            return rule;
        }

        public String getLine() {
            return line;
        }

        public String getTextRange() {
            return text_range;
        }

        public String getFlows() { return flows; }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getAuthor() {
            return author;
        }

        public String getTags() {
            return tags;
        }

        public String getCreationDate() {
            return creation_date;
        }

        public String getUpdateDate() {
            return update_date;
        }

        public String getSeverity() {
            return severity;
        }

        public String getType() { return type; }
    }


    public void getComponent() throws IOException {
        ArrayList<Component> component = new ArrayList<>();

        List<String> componentId = new ArrayList<>();
        List<String> componentKey = new ArrayList<>();
        String scomponentId;
        String scomponentKey;
        String response;
        List<String> keys = readFile("ProjectKey.txt");
        for (int i = 0; i < keys.size(); i++) {

            String component_uri = "http://localhost:9000/api/components/show?key=" + keys.get(i);
            response = getHTTPConnection(component_uri);
            JSONObject jsonObject = new JSONObject(response);
            System.out.println(jsonObject.get("component"));
            componentId.add(((JSONObject) jsonObject.get("component")).get("id").toString());
            componentKey.add(((JSONObject) jsonObject.get("component")).get("key").toString());
            scomponentId = ((JSONObject) jsonObject.get("component")).get("id").toString();
            scomponentKey = ((JSONObject) jsonObject.get("component")).get("key").toString();
            component.add(new Component(scomponentId, scomponentKey));

        }


        getMeasures(componentId);
        getIssues(componentKey);

    }

    public void getMeasures(List<String> componentId) throws IOException {
        ArrayList<Project> project = new ArrayList<>();
        ArrayList<Measure> measure = new ArrayList<>();
        String component_uri, response;
        //String id, key, name;

        String id;
        String key;
        String name;
        ArrayList<String> metric;
        ArrayList<String> value;
        for (int i = 0; i < componentId.size(); i++) {
            component_uri = "http://localhost:9000/api/measures/component?componentId=" + componentId.get(i) + "&metricKeys=test_success_density,statements," +
                    "functions,ncloc_language_distribution,ncloc,lines,files,directories,security_remediation_effort,security_rating," +
                    "vulnerabilities,reliability_remediation_effort,reliability_rating,bugs,sqale_debt_ratio,sqale_index,sqale_rating," +
                    "code_smells,critical_violations,info_violations,blocker_violations,minor_violations,major_violations,violations," +
                    "complexity,file_complexity,comment_lines_density,duplicated_blocks,duplicated_files,duplicated_lines,duplicated_lines_density";
            response = getHTTPConnection(component_uri);
            JSONObject jsonObject = new JSONObject(response);
            id = ((JSONObject) jsonObject.get("component")).get("id").toString();
            key = ((JSONObject) jsonObject.get("component")).get("key").toString();
            name = ((JSONObject) jsonObject.get("component")).get("name").toString();
            project.add(new Project(id, name, key));

            JSONArray arrayMeasure = (JSONArray) ((JSONObject) jsonObject.get("component")).get("measures");
            metric = new ArrayList<>();
            value = new ArrayList<>();
            for (int j = 0; j < arrayMeasure.length(); j++) {

                metric.add(((JSONObject) arrayMeasure.get(j)).get("metric").toString());
                value.add(((JSONObject) arrayMeasure.get(j)).get("value").toString());
            }
            measure.add(new Measure(metric, value));
        }

        dbConnectionObj.InsertMeasure(project, measure);
    }

    public void getIssues(List<String> componentKey) throws IOException {
        String component_uri, response;
        String[] severity = new String[]{"INFO", "MINOR", "MAJOR", "CRITICAL", "BLOCKER"};
        String[] type = new String[]{"CODE_SMELL", "BUG", "VULNERABILITY"};
        ArrayList<IssueComponent> issueComponent = new ArrayList<>();
        ArrayList<Issues> issues = new ArrayList<>();
        String uuid, project_id, file_name, file_path, component_id;
        String key, project="", comp_id="", rule="", line="", text_range="", flows="", status="", message="",author="", tags="", issue_severity="", issue_type="";
        String creation_date="", update_date="";

        //String key,rule,component,compId,project,line,status,message,author,creationDate,updateDate;
        JSONObject jsonObject;
        for (int m = 0; m < componentKey.size(); m++) {

            Map<String, Integer> map_comp_id = new HashMap<>();
            component_uri = "http://localhost:9000/api/issues/search?componentKeys=" + componentKey.get(m);
            response = getHTTPConnection(component_uri);
            jsonObject = new JSONObject(response);
            int total_issues = (int) jsonObject.get("total");
            //JSONArray arrayTextRange, arrayFlows, arrayTags;
            boolean IsIssue1000 = false;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 3; j++) {
                    component_uri = "http://localhost:9000/api/issues/search?componentKeys=" + componentKey.get(m) + "&ps=20&severities=" + severity[i] + "&types=" + type[j];
                    response = getHTTPConnection(component_uri);
                    jsonObject = new JSONObject(response);
                    //System.out.println(response);
                    int total_issues_param = (int) jsonObject.get("total");
                    if (total_issues_param > 10000) {
                        IsIssue1000 = true;
                        break;
                    }
                }
            }
            if (!IsIssue1000) {
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 3; j++) {
                        component_uri = "http://localhost:9000/api/issues/search?componentKeys=" + componentKey.get(m) + "&ps=20&severities=" + severity[i] + "&types=" + type[j];
                        response = getHTTPConnection(component_uri);
                        jsonObject = new JSONObject(response);
                        //System.out.println(response);
                        int total_issues_param = (int) jsonObject.get("total");
                        int total_pages = (int) Math.ceil((double) total_issues_param / 200);
                        for (int k = 0; k < total_pages; k++) {
                            component_uri = "http://localhost:9000/api/issues/search?componentKeys=" + componentKey.get(m) + "&p=" + (int) (k + 1) + "&ps=200&severities=" + severity[i] + "&types=" + type[j];
                            System.out.println(component_uri);
                            response = getHTTPConnection(component_uri);
                            jsonObject = new JSONObject(response);
                            //System.out.println(response);
                            //System.out.println("severity" + severity[i] + " type" + type[j] + " total_param" + total_issues_param + " total_pages" + total_pages);

                            JSONArray arrayComponents = (JSONArray) (jsonObject.get("components"));

                            for (int l = 0; l < arrayComponents.length(); l++) {
                                if (((JSONObject) arrayComponents.get(l)).get("key").toString().compareTo(componentKey.get(m)) != 0) {
                                    component_id = ((JSONObject) arrayComponents.get(l)).get("id").toString();
                                    if (!map_comp_id.containsKey(component_id)) {
                                        map_comp_id.put(component_id, 1);
                                        project_id = ((JSONObject) arrayComponents.get(l)).get("key").toString().split(":")[0];
                                        uuid = ((JSONObject) arrayComponents.get(l)).get("uuid").toString();
                                        file_name = ((JSONObject) arrayComponents.get(l)).get("name").toString();
                                        file_path = ((JSONObject) arrayComponents.get(l)).get("path").toString();

                                        issueComponent.add(new IssueComponent(uuid, project_id, file_name, file_path, component_id));
                                    }
                                }
                            }

                            JSONArray arrayIssues = (JSONArray) (jsonObject.get("issues"));
                            for (int l = 0; l < arrayIssues.length(); l++) {
                                //System.out.println("severity: " + severity[i] + " type: " + type[j] + l + ":" + ((JSONObject) arrayIssues.get(l)).get("key"));
                                System.out.println("arrayissue: " + arrayIssues);
                                key = ((JSONObject) arrayIssues.get(l)).get("key").toString();
                                project = ((JSONObject) arrayIssues.get(l)).get("project").toString();
                                comp_id = ((JSONObject) arrayIssues.get(l)).get("componentId").toString();
                                rule = ((JSONObject) arrayIssues.get(l)).get("rule").toString();
                                if (((JSONObject) arrayIssues.get(l)).has("line")) {
                                    line = ((JSONObject) arrayIssues.get(l)).get("line").toString();
                                }
                                if (((JSONObject) arrayIssues.get(l)).has("textRange")) {
                                    text_range = ((JSONObject) arrayIssues.get(l)).get("textRange").toString();
                                }

                                flows = ((JSONObject) arrayIssues.get(l)).get("flows").toString();
                                status = ((JSONObject) arrayIssues.get(l)).get("status").toString();
                                message = ((JSONObject) arrayIssues.get(l)).get("message").toString();
                                author = ((JSONObject) arrayIssues.get(l)).get("author").toString();
                                tags = ((JSONObject) arrayIssues.get(l)).get("tags").toString();
                                issue_severity = ((JSONObject) arrayIssues.get(l)).get("severity").toString();
                                issue_type = ((JSONObject) arrayIssues.get(l)).get("type").toString();
                                creation_date = ((JSONObject) arrayIssues.get(l)).get("creationDate").toString();
                                update_date = ((JSONObject) arrayIssues.get(l)).get("updateDate").toString();
                                issues.add(new Issues(key, comp_id, project, rule, line, text_range, flows, status, message, author, tags, creation_date, update_date, issue_severity, issue_type));

                            }


                        }
                    }
                }
            }
            dbConnectionObj.InsertIssues(issueComponent, issues);
            System.out.println(issueComponent.size());
        }

    }

    public String getHTTPConnection(String uri) throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(uri);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpGet);) {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String entitystring = EntityUtils.toString(entity);
            return entitystring;
        }
    }

}
