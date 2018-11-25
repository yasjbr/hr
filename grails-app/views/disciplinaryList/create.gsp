<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryList.entity', default: 'Loan List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'Loan List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'disciplinaryList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="disciplinaryListForm"
                                     callLoadingFunction="performPostActionWithEncodedId"
                                     callBackFunction="resetAttachmentData"
                                     controller="disciplinaryList" action="save">
                <el:hiddenField name="templateCoverLetterId" />
                <g:render template="/disciplinaryList/form" model="[disciplinaryList:disciplinaryList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton isSubmit="false" functionName="attachment" onclick="openAttachmentModalForPreCreate()"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>


<g:render template="/attachment/attachmentPreCreateTemplate" model="[
        formName:'disciplinaryListForm' ,
        referenceObject:referenceObject ,
        operationType:operationType,
        attachmentTypeList:attachmentTypeList
]"/>

</body>
</html>
