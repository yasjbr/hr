<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'personArrestHistory.entity', default: 'personArrestHistory List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'personArrestHistory List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'personArrestHistory', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm callBackGeneralFunction="successCallBack" name="personArrestHistoryForm"
                                     controller="personArrestHistory" action="selectEmployee">
                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  : 6]"/>
                <el:formButton functionName="select" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
<script>
    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'personArrestHistory',action: 'create')}?person.encodedId=" + json.encodedPersonId;
        }
    }
</script>
</body>
</html>
