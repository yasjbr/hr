<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeePromotion.entity', default: 'EmployeePromotion List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'EmployeePromotion List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'employeePromotion',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="employeePromotionForm" controller="employeePromotion" action="update">
                <g:render template="/employeePromotion/form" model="[employeePromotion:employeePromotion]"/>
                <el:hiddenField name="id" value="${employeePromotion?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>