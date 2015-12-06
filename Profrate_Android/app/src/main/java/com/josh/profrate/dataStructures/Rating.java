package com.josh.profrate.dataStructures;

import com.appspot.profrate_1148.profrateAPI.model.ProfrateRatingMessage;

public class Rating {

    public final double personality;
    public final double teaching_skill;
    public final double research_skill;
    public final double knowledge_level;

    Rating(ProfrateRatingMessage rating){
        this.personality = rating.getPersonality();
        this.teaching_skill = rating.getTeachingSkill();
        this.research_skill = rating.getResearchSkill();
        this.knowledge_level = rating.getKnowledgeLevel();
    }

    public Rating(double personality, double teaching_skill, double research_skill, double knowledge_level){
        if(personality < 0.0)
            personality = 0.0;
        if(personality > 5.0)
            personality = 5.0;
        if(teaching_skill < 0.0)
            teaching_skill = 0.0;
        if(teaching_skill > 5.0)
            teaching_skill = 5.0;
        if(research_skill < 0.0)
            research_skill = 0.0;
        if(research_skill > 5.0)
            research_skill = 5.0;
        if(knowledge_level < 0.0)
            knowledge_level = 0.0;
        if(knowledge_level > 5.0)
            knowledge_level = 5.0;
        this.personality = personality;
        this.teaching_skill = teaching_skill;
        this.research_skill = research_skill;
        this.knowledge_level = knowledge_level;
    }

    public double overallRating(){
        return (personality + teaching_skill + research_skill + knowledge_level)/4;
    }
}
