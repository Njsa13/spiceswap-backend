package com.spiceswap.spiceswap.dto.request;

import com.spiceswap.spiceswap.validation.ValidSlug;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.spiceswap.spiceswap.common.util.Constants.ErrorMessage.INVALID_SLUG;
import static com.spiceswap.spiceswap.common.util.Constants.ValidationMessage.NOT_BLANK;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateIngredientDetailRequest {
    @ValidSlug(message = INVALID_SLUG)
    private String ingredientDetailSlug;

    @NotBlank(message = NOT_BLANK)
    private String ingredientName;

    @NotBlank(message = NOT_BLANK)
    private String dose;
}
