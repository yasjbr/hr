<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'joinedFirmOperationDocument.entity', default: 'JoinedFirmOperationDocument List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'JoinedFirmOperationDocument List')}" />
    <title>${title}</title>
    <g:render template="script"/>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'joinedFirmOperationDocument',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="joinedFirmOperationDocumentForm" callBackFunction="successCallBack" callLoadingFunction="performPostActionWithEncodedId" controller="joinedFirmOperationDocument" action="save">
                <g:render template="/joinedFirmOperationDocument/form" model="[joinedFirmOperationDocument:joinedFirmOperationDocument]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'joinedFirmOperationDocument',action:'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
<script>
    function getOperationSelectElement() {
        $.ajax({
            url: '${createLink(controller: 'joinedFirmOperationDocument',action: 'getOperationSelectElement')}',
            type: 'POST',
            dataType: 'html',
            beforeSend: function (jqXHR, settings) {
                guiLoading.show();
            }
            ,
            error: function (jqXHR) {
                guiLoading.hide();
            }
            ,
            success: function (html) {
                guiLoading.hide();
                $('#operationSelectElement').html(html);
                $('#operation').chosen({allow_single_deselect: true,placeholder_text_single: "${message(code:'default.choose.label')}",placeholder_text_multiple:"${message(code:'default.choose.label')}"});
            }
        });
    }
    function successCallBack(json) {
        if (json.success) {
            clearDocumentsTable();
            getOperationSelectElement();
        }
    }
    $(document).ready(function () {
        getOperationSelectElement();
    });
</script>
</body>
</html>
