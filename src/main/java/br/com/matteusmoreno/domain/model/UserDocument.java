package br.com.matteusmoreno.domain.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class UserDocument {

    private String cnh;
    private String cpfUrl;
    private String rgUrl;
    private String proofOfResidenceUrl;
    private String criminalRecordUrl;
    private String passportUrl;

}
