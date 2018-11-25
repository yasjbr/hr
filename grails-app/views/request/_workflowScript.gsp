<%--
  Created by IntelliJ IDEA.
  User: wassi
  Date: 02/08/18
  Time: 12:28
--%>

<script>
    /**
     * this function used only for submit form and start workflow on request
     * check if the saveWithWorkflow clicked button then add hidden filed to form before submit form
     * otherwise remove the hidden filed.
     */
    function submitWithWorkflowFunction() {
        var input = document.createElement("input");
        input.setAttribute("type", "hidden");
        input.setAttribute("id", "workflowHiddenField");
        input.setAttribute("name", "saveWithWorkflow");
        input.setAttribute("value", "true");
        document.getElementById("${formName}").appendChild(input);
        $("#${formName}").submit();
    }
</script>