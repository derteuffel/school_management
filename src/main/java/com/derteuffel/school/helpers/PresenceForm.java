package com.derteuffel.school.helpers;

import com.derteuffel.school.entities.Presence;
import lombok.Data;

import java.util.Collection;

/**
 * Created by user on 11/04/2020.
 */
@Data
public class PresenceForm {

    private Boolean status;
    private Collection<Presence> presences;
}
