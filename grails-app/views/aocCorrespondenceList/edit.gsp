<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'EnumCorrespondenceDirection.' + aocCorrespondenceList.correspondenceDirection, default: 'Incoming')}"/>
    <g:set var="title"
           value="${message(code: 'default.edit.label', args: [entity], default: 'AocCorrespondenceList List')}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: listController, action: listAction)}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="aocCorrespondenceListForm" controller="aocCorrespondenceList" action="update">
                <el:hiddenField name="id" value="${aocCorrespondenceList?.id}"/>
                %{--<el:hiddenField name="hrCorrespondenceList.id"--}%
                                %{--value="${aocCorrespondenceList?.hrCorrespondenceList?.id}"/>--}%
                <g:render template="/aocCorrespondenceList/formAoc"
                          model="[aocCorrespondenceList: aocCorrespondenceList]"/>
                <el:formButton isSubmit="true" functionName="save" withClose="true" withPreviousLink="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>