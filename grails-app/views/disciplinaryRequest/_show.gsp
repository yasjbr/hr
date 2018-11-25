<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: disciplinaryRequest?.employee]"/>
<el:row/>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${message(code: 'request.info.label')}">
    <g:render template="/request/wrapperRequestShow" model="[request: disciplinaryRequest]"/>
    <lay:showElement value="${disciplinaryRequest?.disciplinaryCategory}" type="DisciplinaryCategory" label="${message(code:'disciplinaryRequest.disciplinaryCategory.label',default:'disciplinaryCategory')}" />
    <lay:showElement value="${disciplinaryRequest?.requestStatusNote}" type="String" label="${message(code:'disciplinaryRequest.requestStatusNote.label',default:'requestStatusNote')}" />
</lay:showWidget>
<el:row/>
<el:row/>
<el:row/>
<g:render template="/request/wrapperShow" model="[request: disciplinaryRequest]"/>
<el:row/>
<g:render template="/request/wrapperManagerialOrderShow" model="[request: disciplinaryRequest, colSize: 12]"/>
<el:row/>
<lay:table title="${message(code:'disciplinaryRequest.violationsAndJudgments.label')}" styleNumber="1" >
    <lay:tableHead title="${message(code:'disciplinaryRequest.disciplinaryReasons.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRequest.disciplinaryJudgment.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.value.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.unitAndCurrency.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.fromDate.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.toDate.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.orderNo.label')}"/>
    <lay:tableHead title="${message(code:'disciplinaryRecordJudgment.note.label')}"/>

    <g:each in="${disciplinaryRequest?.disciplinaryJudgments?.toList()?.sort{it.disciplinaryJudgment.descriptionInfo}}" var="disciplinaryRecordJudgment">
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
<div class="row">
</div>