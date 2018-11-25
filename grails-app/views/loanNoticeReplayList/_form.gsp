<el:formGroup>
    <el:textField name="name" size="8" class=" isRequired"
                  label="${message(code: 'loanNoticeReplayList.name.label', default: 'name')}"
                  value="${loanNoticeReplayList?.name}"/>
</el:formGroup>

<el:formGroup>
    <el:select valueMessagePrefix="EnumReceivingParty" from="${ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.values()}"
               name="receivingParty" size="8" class=""
               label="${message(code: 'loanNoticeReplayList.receivingParty.label', default: 'receivingParty')}"
               value="${loanNoticeReplayList?.receivingParty}"/>
</el:formGroup>
<el:formGroup>
    <el:textAreaDescription name="coverLetter" size="8" class=" "
                            label="${message(code: 'loanNoticeReplayList.coverLetter.label', default: 'coverLetter')}"
                            value="${loanNoticeReplayList?.coverLetter}" />

    <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-round width-135"
                  link="${createLink(controller: 'correspondenceTemplate', action: 'listModal')}"
                  label="">
        <i class="fa fa-hand-o-up"></i>
        <g:message code="default.button.select.label" />
    </el:modalLink>
</el:formGroup>
