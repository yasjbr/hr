<el:formGroup>
    <el:textField name="name"
                  size="8"
                  class=" isRequired"
                  label="${message(code: 'serviceList.name.label', default: 'name')}"
                  value="${serviceList?.name}"/>
</el:formGroup>

<el:hiddenField name="serviceListType" value="${serviceList?.serviceListType}" />

<el:formGroup>
    <el:select valueMessagePrefix="EnumReceivingParty" from="${ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.values()}"
               name="receivingParty" size="8" class=""
               label="${message(code: 'serviceList.receivingParty.label', default: 'receivingParty')}"
               value="${serviceList?.receivingParty}"/>
</el:formGroup>

<el:formGroup>
    <el:textAreaDescription name="coverLetter" size="8" class=" "
                            label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"
                            value="${serviceList?.coverLetter}" />

    <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-round width-135"
                  link="${createLink(controller: 'correspondenceTemplate', action: 'listModal')}"
                  label="">
        <i class="fa fa-hand-o-up"></i>
        <g:message code="default.button.select.label" />
    </el:modalLink>
</el:formGroup>

