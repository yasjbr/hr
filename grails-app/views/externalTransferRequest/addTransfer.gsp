<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="title" value="${message(code: 'externalTransferRequest.addTransfer.label', default: 'add transfer')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'externalTransferRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm
                    callLoadingFunction="performPostActionWithEncodedId"
                    name="externalTransferRequestForm" controller="externalTransferRequest"
                    action="saveTransfer">

                <el:hiddenField name="id" value="${externalTransferRequest?.id}" />


                <g:render template="requestInfoWrapper" />

                <lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "externalTransferRequest.transferInfo.label")}">
                    <lay:widgetBody>

                        <el:formGroup>


                            <el:textField name="transferPermissionOrderNo" size="6"  class=" isRequired"
                                          label="${message(code:'externalTransferRequest.transferPermissionOrderNo.label',default:'transferOrderNo')}"
                                          value="${externalTransferRequest?.transferPermissionOrderNo}"/>


                            <el:dateField name="transferPermissionDate"  size="6" class=" isRequired" isMaxDate="true"
                                          label="${message(code:'externalTransferRequest.transferPermissionDate.label',default:'transferDate')}"
                                          value="${externalTransferRequest?.transferPermissionDate}" />



                        </el:formGroup>

                        <el:formGroup>

                            <el:textArea name="transferPermissionNote" size="6"
                                         class=" " label="${message(code:'externalTransferRequest.transferPermissionNote.label',default:'transferNote')}"
                                         value="${externalTransferRequest?.transferPermissionNote}"/>

                        </el:formGroup>


                    </lay:widgetBody>

                </lay:widget>


                <el:formButton functionName="saveAndClose" withClose="true" isSubmit="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true" />

            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>