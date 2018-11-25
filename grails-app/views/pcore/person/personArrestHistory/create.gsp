<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'PersonArrestHistory List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personArrestHistory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
            <msg:page />
            <el:validatableResetForm callBackGeneralFunction="successCallBack" name="personArrestHistoryForm"
                                     controller="personArrestHistory" action="save">
                <g:render template="/pcore/person/personArrestHistory/form" model="[personArrestHistory:personArrestHistory]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true" onClick="clickedButton='saveAndCreate'"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" onClick="clickedButton='saveAndClose'"/>
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'PersonArrestHistory',action:'list')}'"/>
            </el:validatableResetForm>
    </lay:widgetBody>
</lay:widget>
<script>
    var clickedButton = '';
    function successCallBack(json) {
        if (json.success) {
            if(clickedButton =="saveAndCreate"){
                window.location.href = "${createLink(controller: 'personArrestHistory',action: 'preCreate')}";
            }
            else if(clickedButton =="saveAndClose"){
                window.location.href = "${createLink(controller: 'personArrestHistory',action: 'list')}";
            }
            clickedButton = '';
        }
    }
</script>
</body>
</html>
