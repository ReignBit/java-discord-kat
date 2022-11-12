package com.reign.kat.lib.converters;

import com.reign.kat.lib.command.ContextEventAdapter;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberConverter extends Converter<Member> {
    private static final Logger log = LoggerFactory.getLogger(MemberConverter.class);

    public MemberConverter(String argName, String description, Member defaultMember)
    {
        super(argName, description, defaultMember, Member.class);
    }

    public MemberConverter(String argName, String description, Member defaultMember, boolean optional)
    {
        super(argName, description, defaultMember, Member.class);
        setOptional(optional);
    }

    @Override
    public Converter<Member> convert(String toConvert, ContextEventAdapter event) throws IllegalArgumentException{
        if (toConvert == null) { set(null); return this; }
        if (toConvert.length() == 18)
        {
            log.info(toConvert);
            Member m = event.getGuild().getMemberById(toConvert);
            log.info("Member = {}", m);
            if (m != null) {
                set(m);
                return this;
            }
        }
        else if (toConvert.startsWith("<@"))
        {
            // mention (<@123123123123123>)
            Member m = event.getGuild().getMemberById(toConvert.substring(2, toConvert.length()-1));
            if (m != null) {
                set(m);
                return this;
            }
        }
        else
        {
            throw new IllegalArgumentException(String.format("Tried to convert %s into Member and failed!",toConvert));
        }
        return null;
    }
}
