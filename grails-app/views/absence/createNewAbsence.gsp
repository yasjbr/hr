<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'absence.entity', default: 'DispatchRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'DispatchRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <el:modalLink link="${createLink(controller: 'absence',action: 'previousAbsenceModal', params:[employeeEncodedId: absence?.employee?.encodedId, absenceEncodedId: null])}"
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
            <el:validatableResetForm name="absenceForm" callLoadingFunction="performPostActionWithEncodedId" controller="absence" action="save">
                <g:render template="/absence/form" model="[absence:absence]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" onClick="window.location.href='${createLink(controller: 'absence', action: 'create')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
