<el:hiddenField name="disciplinaryRequest.id" value="${petitionRequest?.disciplinaryRequest?.id}"/>
<el:hiddenField name="employee.id" value="${petitionRequest?.employee?.id}"/>

<g:render template="/employee/wrapperForm" model="[employee: petitionRequest?.employee]"/>

<br/>

<lay:widget transparent="true" color="blue" icon="icon-asterisk"
            title="${g.message(code: "petitionRequest.disciplinaryRequest.label")}">
    <lay:widgetBody>
        <lay:showWidget size="6">
            <lay:showElement
                    value="${petitionRequest?.disciplinaryRequest?.id}"
                    type="String"
                    label="${message(code: 'disciplinaryRequest.id.label', default: 'id')}"/>
            <lay:showElement
                    value="${petitionRequest?.disciplinaryRequest?.requestDate}"
                    type="ZonedDate"
                    label="${message(code: 'disciplinaryRequest.requestDate.label', default: 'requestDate')}"/>
        </lay:showWidget>
        <lay:showWidget size="6">
            <lay:showElement
                    value="${petitionRequest?.disciplinaryRequest?.disciplinaryCategory}"
                    type="DisciplinaryCategory"
                    label="${message(code: 'disciplinaryRequest.disciplinaryCategory.label', default: 'disciplinaryCategory')}"/>
            <lay:showElement
                    value="${petitionRequest?.disciplinaryRequest?.requestStatusNote}"
                    type="String"
                    label="${message(code: 'disciplinaryRequest.requestStatusNote.label', default: 'requestStatusNote')}"/>
        </lay:showWidget>
    </lay:widgetBody>
</lay:widget>
<el:row/>
<br/>


<el:row/>
<lay:table title="${message(code:'petitionRequest.disciplinaryRequest.violationsAndJudgments.label')}" styleNumber="1" >
    <lay:tableHead title="${message(code:'disciplinaryRequest.disciplinaryReasons.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRequest.disciplinaryJudgment.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.value.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.unitAndCurrency.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.fromDate.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.toDate.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.orderNo.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.note.label')}"/>

    <g:each in="${petitionRequest?.disciplinaryRequest?.disciplinaryJudgments?.toList()?.sort{it.disciplinaryJudgment.descriptionInfo}}" var="disciplinaryRecordJudgment">
        <rowElement><tr class='center'>
            <td width="25%" class='center'>${disciplinaryRecordJudgment?.disciplinaryReasons?.toList()?.sort{it.descriptionInfo}?.descriptionInfo?.join(",")}</td>
            <td class='center'>${disciplinaryRecordJudgment?.disciplinaryJudgment}</td>
            <td class='center'>${disciplinaryRecordJudgment?.value}</td>
            <g:if test="${disciplinaryRecordJudgment?.currencyId}">
                <td class='center'>${disciplinaryRecordJudgment?.transientData?.currencyDTO}</td>
            </g:if>
            <g:else>
                <td class='center'>${disciplinaryRecordJudgment?.transientData?.unitDTO}</td>
            </g:else>
            <td class='center'>${disciplinaryRecordJudgment?.fromDate?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}</td>
            <td class='center'>${disciplinaryRecordJudgment?.toDate?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}</td>
            <td class='center'>${disciplinaryRecordJudgment?.disciplinaryListNote?.orderNo}</td>
            <td class='center'>${disciplinaryRecordJudgment?.disciplinaryListNote?.note}</td>
        </tr></rowElement>

    </g:each>
</lay:table>


<br/>


<el:row/>
<lay:widget transparent="true" color="blue" icon="icon-info-4"
            title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired"
                          label="${message(code: 'petitionRequest.requestDate.label', default: 'requestDate')}"
                          value="${petitionRequest?.requestDate}"/>
            <el:textArea name="requestStatusNote" size="6" class=""
                         label="${message(code: 'petitionRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${petitionRequest?.requestStatusNote}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<br/>

<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:render template="/request/wrapperManagerialOrder" model="[request: petitionRequest, formName:'petitionRequestForm']"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>


<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>
