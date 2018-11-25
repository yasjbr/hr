<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'endOfService.entity', default: 'endOfService List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'endOfService List')}" />
    <title>${title}</title>
</head>
<body>
<script>
    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'employmentServiceRequest',action: 'listEndOfService')}";
        }
    }
</script>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'employmentServiceRequest',action:'listEndOfService')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="employmentServiceRequestForm" callBackFunction="successCallBack" controller="employmentServiceRequest" action="save">
                <g:render template="/employmentServiceRequest/formEndOfService" model="[employmentServiceRequest:employmentServiceRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
