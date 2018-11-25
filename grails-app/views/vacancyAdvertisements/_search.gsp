<el:formGroup>
    <el:decimalField name="id" size="6" class=" isDecimal"
                     label="${message(code: 'vacancyAdvertisements.id.label', default: 'id')}"/>
    <el:textField name="title" size="6" class=""
                  label="${message(code: 'vacancyAdvertisements.title.label', default: 'title')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="recruitmentCycle"
                     action="autocomplete" name="recruitmentCycle.id"
                     label="${message(code: 'vacancyAdvertisements.recruitmentCycle.label', default: 'recruitmentCycle')}"/>

    <el:range type="date" name="postingDate" size="6" class=""
              label="${message(code: 'vacancyAdvertisements.postingDate.label', default: 'postingDate')}"/>

</el:formGroup>

<el:formGroup>
    <el:range type="date" name="closingDate" size="6" class=""
              label="${message(code: 'vacancyAdvertisements.closingDate.label', default: 'closingDate')}"/>

    <el:textField name="toBePostedOn" size="6" class=""
                  label="${message(code: 'vacancyAdvertisements.toBePostedOn.label', default: 'toBePostedOn')}"/>
</el:formGroup>
