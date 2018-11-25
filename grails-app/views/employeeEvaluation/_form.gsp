<el:hiddenField name="employee.id" value="${employeeEvaluation?.employee?.id}"/>

<el:hiddenField name="evaluationTemplate.id" value="${employeeEvaluation?.evaluationTemplate?.id}"/>

<g:render template="/employee/wrapperForm" model="[employee: employeeEvaluation?.employee]"/>

<br/>
<lay:widget transparent="true" color="blue" icon="icon-info-4"
            title="${employeeEvaluation?.evaluationTemplate?.descriptionInfo?.localName}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="fromDate"  size="6" class="" label="${message(code:'employeeEvaluation.fromDate.label',default:'fromDate')}" value="${employeeEvaluation?.fromDate}" />
            <el:dateField name="toDate"  size="6" class="" label="${message(code:'employeeEvaluation.toDate.label',default:'toDate')}" value="${employeeEvaluation?.toDate}" />
        </el:formGroup>
    </lay:widgetBody>
</lay:widget>
<br/>

<g:each in="${employeeEvaluation?.evaluationTemplate?.availableSections}" var="section">
    <lay:accordion>
        <lay:panel title="${section?.descriptionInfo?.localName}" isCollapsed="false" id="${item?.id}">
            <g:each in="${section?.availableItems}" var="item" status="itemIndex">
                <lay:wall size="lg" style="background-color:#FBFFFF;">
                    <el:hiddenField
                            name="itemId"
                            value="${item?.id}"/>
                    <el:formGroup>
                        <el:textField class=""
                                      size="6"
                                      isDisabled="true"
                                      name="answer-${item?.id}"
                                      value="${item?.descriptionInfo?.localName}"
                                      label="${item?.index}) " helpPopupText="${message(code:'employeeEvaluation.maxMarkQuestion.label')}" helpPopupTextDetails="${item?.maxMark}" />
                    </el:formGroup>

                    ${item?.transientData?.answer}
                    <el:formGroup>
                        <el:select class=" isRequired" from="${1.0 .. item?.maxMark}"
                                   size="6"
                                   label="${message(code:'employeeEvaluation.mark.label')}"
                                   value="${item?.transientData?.answer}"

                                   name="mark-${item?.id}" />
                    </el:formGroup>
                </lay:wall>
            </g:each>
        </lay:panel>
        <br/>
    </lay:accordion>
</g:each>
<el:formGroup>
    <el:textArea
            size="6"
            name="notes"
            value="${employeeEvaluation?.transientData?.note}"
            label="${message(code:'employeeEvaluation.note.label')}" />
</el:formGroup>
