<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'interview.entities', default: 'Interview List')}"/>
    <g:set var="entity" value="${message(code: 'interview.entity', default: 'Interview')}"/>
    <g:set var="title" value="${message(code: 'default.list.label', args: [entities], default: 'Interview List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="interviewCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'interview', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="interviewSearchForm">
            <g:render template="/interview/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['interviewTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('interviewSearchForm');_dataTables['interviewTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="interviewTable" searchFormName="interviewSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="interview" spaceBefore="true" hasRow="true"
              action="filter" serviceName="interview">

    <el:dataTableAction controller="interview" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show interview')}"/>
    <el:dataTableAction controller="interview" action="edit" showFunction="manageInterviewStatus"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit interview')}"/>
    <el:dataTableAction controller="interview" action="delete" showFunction="manageInterviewStatus"
                        actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete interview')}"/>


    <el:dataTableAction functionName="changeInterviewStatus" actionParams="encodedId" showFunction="manageInterviewStatus"  class=" icon-folder-close" type="function" message="${message(code: 'interview.closeInterview.label', default: 'close the interview')}"/>



</el:dataTable>

<script>
    //to allow edit/delete for interview that status  is OPEN
    function manageInterviewStatus(row) {
        if (row.interviewStatus == "مفتوحة") {
            return true;
        }
        return false;
    }


    function changeInterviewStatus(id){
        $.ajax({
            url: '${createLink(controller: 'interview',action: 'changeInterviewStatus')}',
            type: 'POST',
            data: {
                encodedId:id
            },
            dataType: 'json',
            success: function(data) {
                _dataTables['interviewTable'].draw();
                if(data.success){
                    gui.formValidatable.showSuccessMessage(data.message);
                }else{
                    gui.formValidatable.showErrorMessage(data.message);

                }
            }
        });
    }


</script>
</body>
</html>