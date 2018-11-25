<el:formGroup>
    <el:textField name="name" size="8" class=" isRequired"
                  label="${message(code: 'promotionList.name.label', default: 'name')}"
                  value="${promotionList?.name}"/>
</el:formGroup>

<el:formGroup>
    <el:select valueMessagePrefix="EnumReceivingParty" from="${ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.values()}"
               name="receivingParty" size="8" class=""
               label="${message(code: 'promotionList.receivingParty.label', default: 'receivingParty')}"
               value="${promotionList?.receivingParty}"/>
</el:formGroup>

<el:formGroup>
    <el:textAreaDescription name="coverLetter" size="8" class=" "
                            label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"
                            value="${promotionList?.coverLetter}" />

    <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-round width-135"
                  link="${createLink(controller: 'correspondenceTemplate', action: 'listModal')}"
                  label="">
        <i class="fa fa-hand-o-up"></i>
        <g:message code="default.button.select.label" />
    </el:modalLink>
</el:formGroup>
