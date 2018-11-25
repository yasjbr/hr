<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'promotionListEmployee.entity', default: 'PromotionListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PromotionListEmployee List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'promotionListEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="promotionListEmployeeForm" controller="promotionListEmployee" action="update">
                <g:render template="/promotionListEmployee/form" model="[promotionListEmployee:promotionListEmployee]"/>
                <el:hiddenField name="id" value="${promotionListEmployee?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>