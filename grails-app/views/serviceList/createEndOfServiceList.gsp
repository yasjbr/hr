<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'endOfServiceList.entity', default: 'endOfServiceList List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'endOfServiceList List')}" />
    <title>${title}</title>
</head>
<body>
<script>
    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'serviceList',action: 'manageServiceList')}?encodedId=" + json.encodedId;
        }
    }
</script>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'serviceList',action:'listEndOfServiceList')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="serviceListForm" callBackFunction="successCallBack" controller="serviceList" action="save">
                <g:render template="/serviceList/form" model="[serviceList:serviceList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
