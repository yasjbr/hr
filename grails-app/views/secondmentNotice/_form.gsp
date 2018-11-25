<el:formGroup>
    <el:textField name="jobTitle" size="8" class=" isRequired"
                  label="${message(code: 'secondmentNotice.jobTitle.label', default: 'jobTitle')}"
                  value="${secondmentNotice?.jobTitle}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRank.id"
                     label="${message(code: 'secondmentNotice.militaryRank.label', default: 'militaryRank')}"
                     values="${[[secondmentNotice?.militaryRank?.id, secondmentNotice?.militaryRank?.descriptionInfo?.localName]]}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8" class=""
                  label="${message(code: 'secondmentNotice.orderNo.label', default: 'orderNo')}"
                  value="${secondmentNotice?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="periodInMonths" size="8" class=" isRequired isNumber"
                     label="${message(code: 'secondmentNotice.periodInMonths.label', default: 'periodInMonths')}"
                     value="${secondmentNotice?.periodInMonths}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="requesterOrganizationId" size="8" class=" isRequired isNumber"
                     label="${message(code: 'secondmentNotice.requesterOrganizationId.label', default: 'requesterOrganizationId')}"
                     value="${secondmentNotice?.requesterOrganizationId}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="fromDate" size="8" class=" isRequired"
                  label="${message(code: 'secondmentNotice.fromDate.label', default: 'fromDate')}"
                  value="${secondmentNotice?.fromDate}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate" size="8" class=" isRequired"
                  label="${message(code: 'secondmentNotice.toDate.label', default: 'toDate')}"
                  value="${secondmentNotice?.toDate}"/>
</el:formGroup>

<el:formGroup>
    <el:textArea name="description" size="8" class=""
                 label="${message(code: 'secondmentNotice.description.label', default: 'description')}"
                 value="${secondmentNotice?.description}"/>
</el:formGroup>