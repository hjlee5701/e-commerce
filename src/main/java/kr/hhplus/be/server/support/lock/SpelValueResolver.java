package kr.hhplus.be.server.support.lock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelValueResolver {

    /**
     * SpEL 표현식을 평가하여 문자열 결과를 반환합니다.
     *
     * @param parameterNames 파라미터 이름 목록 (e.g. {"userId", "orderId"})
     * @param args           실제 메서드 인자 값
     * @param expression     SpEL 표현식 (e.g. "#userId + ':' + #orderId")
     * @return 평가된 문자열 값
     */
    public static String resolve(String[] parameterNames, Object[] args, String expression) {

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(expression).getValue(context, String.class);
    }
}
