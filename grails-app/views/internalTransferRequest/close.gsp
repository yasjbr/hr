<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest List')}" />
    <g:set var="title" value="${message(code: 'internalTransferRequest.closeRequest.label')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'internalTransferRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="internalTransferRequestForm" controller="internalTransferRequest" action="saveClose">


                <el:hiddenField name="id" value="${internalTransferRequest?.id}" />

                <g:render template="/employee/wrapperForm" model="[employee: internalTransferRequest?.employee]"/>




                <lay:widget transparent="true" color="blue" icon="icon-user" title="${g.message(code: "request.info.label")}">
                    <lay:widgetBody>

                        <lay:showWidget size="6">

                            <lay:showElement
                                    value="${internalTransferRequest?.department?.toString()}"
                                    type="String"
                                    label="${message(code: 'internalTransferRequest.department.label', default: 'department')}"/>


                            <lay:showElement
                                    value="${internalTransferRequest?.requestDate}"
                                    type="ZonedDate"
                                    label="${message(code: 'internalTransferRequest.requestDate.label', default: 'requestDate')}"/>


                            <lay:showElement
                                    value="${internalTransferRequest?.orderDate}"
                                    type="ZonedDate"
                                    label="${message(code: 'internalTransferRequest.orderDate.label', default: 'orderDate')}"/>



                            <lay:showElement
                                    value="${internalTransferRequest?.alternativeEmployee}"
                                    type="String"
                                    label="${message(code: 'internalTransferRequest.alternativeEmployee.label', default: 'alternativeEmployee')}"/>



                            <lay:showElement
                                    value="${internalTransferRequest?.requestStatusNote}"
                                    type="String"
                                    label="${message(code: 'internalTransferRequest.requestStatusNote.label', default: 'requestStatusNote')}"/>




                        </lay:showWidget>

                        <lay:showWidget size="6">

                            <lay:showElement value="${internalTransferRequest?.jobTitle?.descriptionInfo}" type="String"
                                             label="${message(code: 'internalTransferRequest.jobTitle.label', default: 'jobTitle')}"/>



                            <lay:showElement value="${internalTransferRequest?.employmentCategory?.descriptionInfo}" type="String"
                                             label="${message(code: 'internalTransferRequest.employmentCategory.label', default: 'employmentCategory')}"/>



                            <lay:showElement value="${internalTransferRequest?.orderNo}" type="String"
                                             label="${message(code: 'internalTransferRequest.orderNo.label', default: 'orderNo')}"/>



                            <lay:showElement value="${internalTransferRequest?.requestReason}" type="String"
                                             label="${message(code: 'internalTransferRequest.requestReason.label', default: 'requestReason')}"/>



                        </lay:showWidget>


                        <el:row/>
                    </lay:widgetBody>
                </lay:widget>


                <lay:widget transparent="true" color="blue" icon="icon-user" title="${g.message(code: "internalTransferRequest.closeInfo.label")}">
                    <lay:widgetBody>


                        <el:formGroup>
                            <el:dateField name="effectiveDate" size="6" class=" isRequired"
                                          label="${message(code: 'internalTransferRequest.effectiveDate.label', default: 'effectiveDate')}" />


                             <el:textField name="orderNo" size="6" class=" isRequired"
                                          label="${message(code: 'internalTransferRequest.orderNo.label', default: 'orderNo')}"
                                           value="${internalTransferRequest?.orderNo}" />
                        </el:formGroup>

                        <el:formGroup>
                            <el:dateField name="orderDate" size="6" class=" isRequired"
                                          label="${message(code: 'internalTransferRequest.orderDate.label', default: 'orderDate')}"
                                          value="${internalTransferRequest?.orderDate}" />


                            <el:textArea name="requestStatusNote" size="6"
                                         class=""
                                         label="${message(code: 'internalTransferRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                                         value="${internalTransferRequest?.requestStatusNote}"/>

                        </el:formGroup>


                    </lay:widgetBody>
                </lay:widget>





                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>