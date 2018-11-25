<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'jobRequirement.entity', default: 'JobRequirement List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'JobRequirement List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'jobRequirement',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="jobRequirementForm" controller="jobRequirement" action="update">
                <g:render template="/jobRequirement/form" model="[jobRequirement:jobRequirement]"/>
                <el:hiddenField name="id" value="${jobRequirement?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>