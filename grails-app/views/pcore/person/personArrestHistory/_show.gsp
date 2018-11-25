<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personArrestHistory?.person}" type="Person" label="${message(code:'personArrestHistory.person.label',default:'person')}" />
    <lay:showElement value="${personArrestHistory?.arrestingClassification}" type="enum" label="${message(code:'personArrestHistory.arrestingClassification.label',default:'arrestingClassification')}" messagePrefix="ArrestingClassification" />
    <lay:showElement value="${personArrestHistory?.arrestingParty}" type="enum" label="${message(code:'personArrestHistory.arrestingParty.label',default:'arrestingParty')}" messagePrefix="ArrestingParty" />
    <lay:showElement value="${personArrestHistory?.arrestDate}" type="ZonedDate" label="${message(code:'personArrestHistory.arrestDate.label',default:'arrestDate')}" />
    <lay:showElement value="${personArrestHistory?.releaseDate}" type="ZonedDate" label="${message(code:'personArrestHistory.releaseDate.label',default:'releaseDate')}" />
    <lay:showElement value="${personArrestHistory?.periodInMonths}" type="Short" label="${message(code:'personArrestHistory.periodInMonths.label',default:'periodInMonths')}" />
    <lay:showElement value="${personArrestHistory?.accusation}" type="String" label="${message(code:'personArrestHistory.accusation.label',default:'accusation')}" />
    <lay:showElement value="${personArrestHistory?.arrestReason}" type="String" label="${message(code:'personArrestHistory.arrestReason.label',default:'arrestReason')}" />
    <g:if test="${personArrestHistory?.jail}">
        <lay:showElement value="${personArrestHistory?.jail}" type="Jail" label="${message(code:'personArrestHistory.jail.label',default:'jail')}" />
    </g:if>
    <g:else>
        <lay:showElement value="${personArrestHistory?.jailName}" type="String" label="${message(code:'personArrestHistory.jail.label',default:'jail')}" />
    </g:else>
    <lay:showElement value="${personArrestHistory?.lawyerName}" type="String" label="${message(code:'personArrestHistory.lawyerName.label',default:'lawyerName')}" />
    <lay:showElement value="${personArrestHistory?.note}" type="String" label="${message(code:'personArrestHistory.note.label',default:'note')}" />
    <lay:showElement value="${personArrestHistory?.isJudgementForEver}" type="boolean" label="${message(code:'personArrestHistory.isJudgementForEver.label',default:'isJudgementForEver')}" />
    <g:if test="${personArrestHistory.arrestJudgementDetails}">
        <div class="profile-info-row">
            <div class="profile-info-name">  ${g.message(code: 'personArrestHistory.arrestPeriod.label')}</div>
            <div class="profile-info-value"><span class="editable"></span>
                <ol id="PeriodDetailsUl" >
                    <g:each in="${personArrestHistory.arrestJudgementDetails?.toList()?.sort{it.arrestPeriod}}" var="arrestJudgementDetail">
                        <li class="well-sm alert alert-success">
                            <g:if test="${arrestJudgementDetail?.unitOfMeasurement}">
                                ${arrestJudgementDetail?.arrestPeriod} - ${arrestJudgementDetail?.unitOfMeasurement?.toString()}
                            </g:if>
                            <g:else>
                                ${arrestJudgementDetail?.arrestPeriod} - ${g.message(code: "ArrestJudgementType."+arrestJudgementDetail?.arrestJudgementType)}
                            </g:else>


                        </li>
                    </g:each>
                </ol>
            </div>
        </div>
    </g:if>
</lay:showWidget>