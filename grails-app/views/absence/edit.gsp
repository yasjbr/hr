<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'absence.entity', default: 'Absence List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'Absence List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <el:modalLink link="${createLink(controller: 'absence',action: 'previousAbsenceModal',params:[employeeEncodedId: absence?.employee?.encodedId, absenceEncodedId: absence?.encodedId])}"
                          preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                          label="${message(code: 'absence.previous.label')}">
                <i class="icon-list"></i>
            </el:modalLink>
            <btn:listButton style="margin-right: 10px;" onClick="window.location.href='${createLink(controller:'absence',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="absenceForm" controller="absence" action="update">
                <g:render template="/absence/form" model="[absence:absence]"/>
                <el:hiddenField name="id" value="${absence?.id}" />
                <el:formButton isSubmit="true" functionName="save"  withPreviousLink="true" />
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>