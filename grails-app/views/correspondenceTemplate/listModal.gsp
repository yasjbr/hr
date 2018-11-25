<g:set var="entities" value="${message(code: 'correspondenceTemplate.entities', default: 'CorrespondenceTemplate List')}" />
<g:set var="entity" value="${message(code: 'correspondenceTemplate.entity', default: 'CorrespondenceTemplate')}" />
<g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'CorrespondenceTemplate List')}" />

<el:modal isModalWithDiv="true"  id="correspondenceTemplateModal"
          title="${message(code:'correspondenceTemplate.entities')}"
          preventCloseOutSide="true" width="80%">
    <g:render template="/correspondenceTemplate/dataTable" model="[title:title,disableTools:true,singleSelect:true]" />
    <el:modalButton functionName="addButton" icon="fa fa-hand-o-up" message="${message(code:'default.button.select.label')}" onclick="addRecord()"/>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize();
    function addRecord() {
        var id = _dataTablesCheckBoxValues['correspondenceTemplateTable'][0];
        if(id) {
            $.ajax({
                url: '${createLink(controller: 'correspondenceTemplate',action: 'getInstance')}',
                type: 'POST',
                data: {
                    id: id
                },
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (json) {
                    guiLoading.hide();
                    $("#coverLetter_editor").html(json.coverLetter);
                    $('#application-modal-main-content').modal("hide");
                }
            });
        }
    }
</script>